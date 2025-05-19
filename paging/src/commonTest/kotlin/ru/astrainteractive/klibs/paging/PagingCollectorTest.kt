package ru.astrainteractive.klibs.paging

import app.cash.turbine.test
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.klibs.paging.collector.IntPagerCollector
import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PageResult
import ru.astrainteractive.klibs.paging.util.loadNextPage
import ru.astrainteractive.klibs.paging.util.submitList
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class PagingCollectorTest {

    @Test
    fun WHEN_empty_result_THEN_last_page() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = PagedListDataSource {
                runCatching { emptyList<String>() }
            }
        )
        intPagingCollector.loadNextPage()
        intPagingCollector.state.value.let { pagingState ->
            assertTrue(pagingState.pageResult is PageResult.LastPage)
        }
    }

    @Test
    fun WHEN_failure_result_THEN_failure_state() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = PagedListDataSource<Int, IntPageContext> {
                runCatching { error("Some error") }
            }
        )
        intPagingCollector.loadNextPage()
        intPagingCollector.state.value.let { pagingState ->
            assertTrue(pagingState.pageResult is PageResult.Failure)
        }
    }

    @Test
    fun WHEN_reset_called_THEN_reset_ToInitial_complete() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = PagedListDataSource<Int, IntPageContext> {
                runCatching { listOf() }
            }
        )
        intPagingCollector.loadNextPage()
        assertTrue(intPagingCollector.state.value.pageResult is PageResult.LastPage)
        intPagingCollector.resetToInitial()
        intPagingCollector.state.value.let { pagingState ->
            assertTrue(pagingState.pageResult is PageResult.Pending)
        }
    }

    @Test
    fun WHEN_has_more_to_load_THEN_not_last_page() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = PagedListDataSource {
                runCatching { List(2) { it } }
            }
        )
        intPagingCollector.loadNextPage()
        intPagingCollector.state.value.let { pagingState ->
            assertTrue(pagingState.pageResult is PageResult.Pending)
        }
    }

    @Test
    fun WHEN_start_loading_THEN_loading() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = PagedListDataSource {
                runCatching { List(2) { it } }
            }
        )
        intPagingCollector.state
            .map { it.pageResult is PageResult.Loading }
            .distinctUntilChanged()
            .test {
                intPagingCollector.loadNextPage()
                assertFalse(awaitItem())
                assertTrue(awaitItem())
                assertFalse(awaitItem())
            }
    }

    @Test
    fun GIVEN_filled_list_WHEN_collecting_THEN_return_given_list() = runTest {
        val size = 10
        val list = List(size) { it }
        val intPagingCollector = IntPagerCollector(
            pager = PagedListDataSource {
                runCatching { list }
            }
        )
        intPagingCollector.state
            .distinctUntilChangedBy { it.items }
            .map { it.items }
            .test {
                assertTrue(awaitItem().isEmpty())
                intPagingCollector.loadNextPage()
                assertContentEquals(awaitItem(), list)
                intPagingCollector.resetToInitial()
                assertTrue(awaitItem().isEmpty())
            }
    }

    @Test
    fun GIVEN_empty_list_WHEN_updating_to_filled_list_THEN_returns_filled_list() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = PagedListDataSource {
                runCatching { emptyList<Int>() }
            }
        )
        intPagingCollector.state
            .map { it.items }
            .test {
                assertTrue(awaitItem().isEmpty())
                listOf(1, 2, 3).let { newList ->
                    intPagingCollector.submitList(newList)
                    assertContentEquals(awaitItem(), newList)
                }
                emptyList<Int>().let { newList ->
                    intPagingCollector.submitList(newList)
                    assertContentEquals(awaitItem(), newList)
                }
            }
    }
}
