package ru.astrainteractive.klibs.paging

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.klibs.paging.collector.IntPagerCollector
import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.fake.FakePagedListDataSource
import ru.astrainteractive.klibs.paging.state.PageResult
import ru.astrainteractive.klibs.paging.state.isFailure
import ru.astrainteractive.klibs.paging.state.isLastPage
import ru.astrainteractive.klibs.paging.state.isLoading
import ru.astrainteractive.klibs.paging.state.throwableOrNull
import ru.astrainteractive.klibs.paging.util.loadNextPage
import ru.astrainteractive.klibs.paging.util.loadPreviousPage
import ru.astrainteractive.klibs.paging.util.resetAndLoadNextPage
import ru.astrainteractive.klibs.paging.util.submitList
import ru.astrainteractive.klibs.paging.util.submitPageContext
import ru.astrainteractive.klibs.paging.util.updatePageContext
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TestFunctionName", "LargeClass")
class PagingCollectorTest {

    @Test
    fun GIVEN_empty_source_WHEN_load_next_page_THEN_last_page() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.LastPage)
    }

    @Test
    fun GIVEN_source_with_items_WHEN_load_next_page_THEN_pending() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(4) { index -> index }, pageSize = 2)
        )
        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.Pending)
    }

    @Test
    fun GIVEN_failing_source_WHEN_load_next_page_THEN_failure_state_carries_original_throwable() = runTest {
        val expected = IllegalStateException("specific failure")
        val dataSource = FakePagedListDataSource(items = List(4) { index -> index }, pageSize = 2)
        dataSource.failure = expected
        val collector = IntPagerCollector(pager = dataSource)

        collector.loadNextPage()
        val pageResult = collector.state.value.pageResult
        assertTrue(pageResult is PageResult.Failure)
        assertEquals(expected, pageResult.throwable)
    }

    @Test
    fun GIVEN_source_with_items_WHEN_load_next_page_THEN_loading_toggles_around_load() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(2) { index -> index }, pageSize = 2)
        )
        collector.state
            .map { pagingState -> pagingState.pageResult is PageResult.Loading }
            .distinctUntilChanged()
            .test {
                collector.loadNextPage()
                assertFalse(awaitItem())
                assertTrue(awaitItem())
                assertFalse(awaitItem())
            }
    }

    @Test
    fun GIVEN_failing_source_WHEN_load_next_page_THEN_pending_then_loading_then_failure_emitted() = runTest {
        val dataSource = FakePagedListDataSource(items = List(2) { index -> index }, pageSize = 2)
        dataSource.failure = IllegalStateException("boom")
        val collector = IntPagerCollector(pager = dataSource)

        collector.state
            .map { pagingState -> pagingState.pageResult }
            .test {
                assertTrue(awaitItem() is PageResult.Pending)
                collector.loadNextPage()
                assertTrue(awaitItem() is PageResult.Loading)
                assertTrue(awaitItem() is PageResult.Failure)
            }
    }

    @Test
    fun GIVEN_custom_initial_page_WHEN_created_THEN_state_starts_at_initial_page() = runTest {
        val collector = IntPagerCollector(
            initialPage = 42,
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        assertEquals(42, collector.state.value.pageContext.page)
        assertTrue(collector.state.value.items.isEmpty())
        assertTrue(collector.state.value.pageResult is PageResult.Pending)
    }

    @Test
    fun GIVEN_source_with_items_WHEN_load_next_page_THEN_page_context_incremented() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(10) { index -> index }, pageSize = 2)
        )
        assertEquals(0, collector.state.value.pageContext.page)
        collector.loadNextPage()
        assertEquals(1, collector.state.value.pageContext.page)
        collector.loadNextPage()
        assertEquals(2, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_source_with_items_WHEN_load_previous_page_THEN_page_context_decremented() = runTest {
        val collector = IntPagerCollector(
            initialPage = 5,
            pager = FakePagedListDataSource(items = List(20) { index -> index }, pageSize = 2)
        )
        assertEquals(5, collector.state.value.pageContext.page)
        collector.loadPreviousPage()
        assertEquals(4, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_source_with_items_WHEN_load_page_with_custom_context_THEN_resulting_context_applied() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(4) { index -> index }, pageSize = 2)
        )
        // Jump two pages forward in a single load.
        collector.loadPage { factory -> factory.next(factory.next(collector.state.value.pageContext)) }
        assertEquals(2, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_filled_list_WHEN_collecting_THEN_returns_given_list() = runTest {
        val list = List(10) { index -> index }
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = list, pageSize = 10)
        )
        collector.state
            .distinctUntilChangedBy { pagingState -> pagingState.items }
            .map { pagingState -> pagingState.items }
            .test {
                assertTrue(awaitItem().isEmpty())
                collector.loadNextPage()
                assertContentEquals(list, awaitItem())
                collector.resetToInitial()
                assertTrue(awaitItem().isEmpty())
            }
    }

    @Test
    fun GIVEN_multiple_pages_WHEN_loading_sequentially_THEN_items_accumulate_in_order() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(6) { index -> index }, pageSize = 2)
        )
        collector.loadNextPage()
        collector.loadNextPage()
        collector.loadNextPage()
        // Re-fetching page 0 would have produced duplicates; ordered accumulation proves the
        // advancing context reached the data source.
        assertContentEquals(listOf(0, 1, 2, 3, 4, 5), collector.state.value.items)
        assertEquals(3, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_last_page_reached_WHEN_load_next_page_THEN_state_unchanged() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(2) { index -> index }, pageSize = 2)
        )
        collector.loadNextPage()
        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.LastPage)
        val stateAtLastPage = collector.state.value

        collector.loadNextPage()
        assertEquals(stateAtLastPage, collector.state.value)
    }

    @Test
    fun GIVEN_failure_WHEN_load_next_page_THEN_stays_failed_until_reset() = runTest {
        val dataSource = FakePagedListDataSource(items = List(4) { index -> index }, pageSize = 2)
        dataSource.failure = IllegalStateException("backend down")
        val collector = IntPagerCollector(pager = dataSource)

        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.Failure)

        // Even after the backend recovers, a failed collector cannot retry without a reset.
        dataSource.failure = null
        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.Failure)
        assertTrue(collector.state.value.items.isEmpty())

        collector.resetToInitial()
        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.Pending)
        assertContentEquals(listOf(0, 1), collector.state.value.items)
    }

    @Test
    fun GIVEN_loaded_items_WHEN_next_load_fails_THEN_items_and_context_preserved() = runTest {
        val dataSource = FakePagedListDataSource(items = List(4) { index -> index }, pageSize = 2)
        val collector = IntPagerCollector(pager = dataSource)

        collector.loadNextPage()
        val itemsAfterFirst = collector.state.value.items
        val pageAfterFirst = collector.state.value.pageContext.page

        dataSource.failure = IllegalStateException("backend down")
        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.Failure)
        assertContentEquals(itemsAfterFirst, collector.state.value.items)
        assertEquals(pageAfterFirst, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_loaded_items_WHEN_next_page_empty_THEN_items_preserved_and_context_not_advanced() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(2) { index -> index }, pageSize = 2)
        )
        collector.loadNextPage()
        val pageAfterFirst = collector.state.value.pageContext.page

        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.LastPage)
        assertContentEquals(listOf(0, 1), collector.state.value.items)
        assertEquals(pageAfterFirst, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_last_page_reached_WHEN_reset_to_initial_THEN_page_result_pending() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        collector.loadNextPage()
        assertTrue(collector.state.value.pageResult is PageResult.LastPage)
        collector.resetToInitial()
        assertTrue(collector.state.value.pageResult is PageResult.Pending)
    }

    @Test
    fun GIVEN_loaded_state_WHEN_resetToInitial_THEN_items_and_context_cleared() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(4) { index -> index }, pageSize = 2)
        )
        collector.loadNextPage()
        assertEquals(1, collector.state.value.pageContext.page)
        assertContentEquals(listOf(0, 1), collector.state.value.items)

        collector.resetToInitial()
        assertEquals(0, collector.state.value.pageContext.page)
        assertTrue(collector.state.value.items.isEmpty())
        assertTrue(collector.state.value.pageResult is PageResult.Pending)
    }

    @Test
    fun GIVEN_loaded_state_WHEN_resetAndLoadNextPage_THEN_state_reset_and_reloaded() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = List(6) { index -> index }, pageSize = 2)
        )
        collector.loadNextPage()
        collector.loadNextPage()
        assertEquals(2, collector.state.value.pageContext.page)
        assertContentEquals(listOf(0, 1, 2, 3), collector.state.value.items)

        collector.resetAndLoadNextPage()
        assertEquals(1, collector.state.value.pageContext.page)
        assertContentEquals(listOf(0, 1), collector.state.value.items)
    }

    @Test
    fun GIVEN_empty_list_WHEN_submitting_filled_then_empty_THEN_items_reflect_each_submit() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        collector.state
            .map { pagingState -> pagingState.items }
            .test {
                assertTrue(awaitItem().isEmpty())
                val filled = listOf(1, 2, 3)
                collector.submitList(filled)
                assertContentEquals(filled, awaitItem())
                collector.submitList(emptyList())
                assertTrue(awaitItem().isEmpty())
            }
    }

    @Test
    fun GIVEN_collector_with_context_WHEN_submit_list_THEN_only_items_change() = runTest {
        val collector = IntPagerCollector(
            initialPage = 2,
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        collector.submitList(listOf(9, 8, 7))
        assertContentEquals(listOf(9, 8, 7), collector.state.value.items)
        assertEquals(2, collector.state.value.pageContext.page)
        assertTrue(collector.state.value.pageResult is PageResult.Pending)
    }

    @Test
    fun GIVEN_collector_WHEN_submit_page_context_THEN_context_replaced() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        collector.submitPageContext(IntPageContext(page = 7))
        assertEquals(7, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_collector_at_page_WHEN_update_page_context_THEN_context_transformed() = runTest {
        val collector = IntPagerCollector(
            initialPage = 3,
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        collector.updatePageContext { context -> context.copy(page = context.page + 10) }
        assertEquals(13, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_collector_WHEN_update_THEN_whole_state_transformed() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(items = emptyList<Int>(), pageSize = 2)
        )
        collector.update { pagingState ->
            pagingState.copy(items = listOf(1, 2, 3), pageResult = PageResult.LastPage)
        }
        assertContentEquals(listOf(1, 2, 3), collector.state.value.items)
        assertTrue(collector.state.value.pageResult is PageResult.LastPage)
    }

    @Test
    fun GIVEN_load_in_progress_WHEN_concurrent_load_requested_THEN_second_request_ignored() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(
                items = List(4) { index -> index },
                pageSize = 2,
                latency = 1.seconds
            )
        )
        val firstLoad = launch { collector.loadNextPage() }
        runCurrent()
        assertTrue(collector.state.value.pageResult is PageResult.Loading)

        // Requested while the first page is still loading -> must be ignored.
        collector.loadNextPage()
        firstLoad.join()

        // A duplicated load would have produced [0, 1, 2, 3] and advanced to page 2.
        assertContentEquals(listOf(0, 1), collector.state.value.items)
        assertEquals(1, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_load_in_progress_WHEN_cancelAndJoin_THEN_in_flight_load_abandoned() = runTest {
        val collector = IntPagerCollector(
            pager = FakePagedListDataSource(
                items = List(4) { index -> index },
                pageSize = 2,
                latency = 1.seconds
            )
        )
        val firstLoad = launch { collector.loadNextPage() }
        runCurrent()
        assertTrue(collector.state.value.pageResult is PageResult.Loading)

        collector.cancelAndJoin()
        firstLoad.join()

        // The in-flight page is discarded: nothing is appended and the context stays at the start.
        assertTrue(collector.state.value.items.isEmpty())
        assertEquals(0, collector.state.value.pageContext.page)
    }

    @Test
    fun GIVEN_page_results_WHEN_inspecting_flags_THEN_extension_flags_match() {
        assertTrue(PageResult.Loading.isLoading)
        assertFalse(PageResult.Pending.isLoading)

        assertTrue(PageResult.LastPage.isLastPage)
        assertFalse(PageResult.Loading.isLastPage)

        val error = IllegalStateException("x")
        val failure = PageResult.Failure(error)
        assertTrue(failure.isFailure)
        assertFalse(PageResult.Pending.isFailure)

        assertEquals(error, failure.throwableOrNull)
        assertNull(PageResult.Loading.throwableOrNull)
    }
}
