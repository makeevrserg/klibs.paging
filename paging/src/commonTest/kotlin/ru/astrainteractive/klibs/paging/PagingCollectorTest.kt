package ru.astrainteractive.klibs.paging

import app.cash.turbine.test
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.data.LambdaPagedListDataSource
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class PagingCollectorTest {

    @Test
    fun WHEN_empty_result_THEN_last_page() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = LambdaPagedListDataSource {
                runCatching { emptyList<String>() }
            }
        )
        intPagingCollector.loadNextPage()
        intPagingCollector.pagingStateFlow.value.let { pagingState ->
            assertTrue(pagingState.isLastPage)
            assertFalse(pagingState.isFailure)
            assertFalse(pagingState.isLoading)
        }
    }

    @Test
    fun WHEN_failure_result_THEN_failure_state() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = LambdaPagedListDataSource<Int, IntPageContext> {
                runCatching { error("Some error") }
            }
        )
        intPagingCollector.loadNextPage()
        intPagingCollector.pagingStateFlow.value.let { pagingState ->
            assertTrue(pagingState.isFailure)
            assertFalse(pagingState.isLastPage)
            assertFalse(pagingState.isLoading)
        }
    }

    @Test
    fun WHEN_reset_called_THEN_reset_complete() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = LambdaPagedListDataSource<Int, IntPageContext> {
                runCatching { listOf() }
            }
        )
        intPagingCollector.loadNextPage()
        assertTrue(intPagingCollector.pagingStateFlow.value.isLastPage)
        intPagingCollector.reset()
        intPagingCollector.pagingStateFlow.value.let { pagingState ->
            assertFalse(pagingState.isFailure)
            assertFalse(pagingState.isLastPage)
            assertFalse(pagingState.isLoading)
        }
    }

    @Test
    fun WHEN_has_more_to_load_THEN_not_last_page() = runTest {
        val intPagingCollector = IntPagerCollector(
            pageSize = 2,
            pager = LambdaPagedListDataSource {
                runCatching { List(2) { it } }
            }
        )
        intPagingCollector.loadNextPage()
        intPagingCollector.pagingStateFlow.value.let { pagingState ->
            assertFalse(pagingState.isFailure)
            assertFalse(pagingState.isLastPage)
            assertFalse(pagingState.isLoading)
        }
    }

    @Test
    fun WHEN_start_loading_THEN_loading() = runTest {
        val intPagingCollector = IntPagerCollector(
            pageSize = 2,
            pager = LambdaPagedListDataSource {
                runCatching { List(2) { it } }
            }
        )
        intPagingCollector.pagingStateFlow
            .map { it.isLoading }
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
            pageSize = size,
            pager = LambdaPagedListDataSource {
                runCatching { list }
            }
        )
        intPagingCollector.listStateFlow
            .test {
                assertTrue(awaitItem().isEmpty())
                intPagingCollector.loadNextPage()
                assertContentEquals(awaitItem(), list)
                intPagingCollector.reset()
                assertTrue(awaitItem().isEmpty())
            }
    }

    @Test
    fun GIVEN_empty_list_WHEN_updating_to_filled_list_THEN_returns_filled_list() = runTest {
        val intPagingCollector = IntPagerCollector(
            pager = LambdaPagedListDataSource {
                runCatching { emptyList<Int>() }
            }
        )
        intPagingCollector.listStateFlow
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
