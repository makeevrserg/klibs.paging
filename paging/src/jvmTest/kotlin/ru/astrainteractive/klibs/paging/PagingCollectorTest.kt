package ru.astrainteractive.klibs.paging

import app.cash.turbine.test
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.klibs.paging.data.LambdaPagedListDataSource
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PagingCollectorTest {

    @Test
    fun `Test last page when empty list`(): Unit = runBlocking {
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
    fun `Test failure`(): Unit = runBlocking {
        val intPagingCollector = IntPagerCollector(
            pager = LambdaPagedListDataSource<Int, Int> {
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
    fun `Test reset`(): Unit = runBlocking {
        val intPagingCollector = IntPagerCollector(
            pager = LambdaPagedListDataSource<Int, Int> {
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
    fun `Test not last page`(): Unit = runBlocking {
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
    fun `Test loading`(): Unit = runBlocking {
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
    fun `Test return list`(): Unit = runBlocking {
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
    fun `Test update list`(): Unit = runBlocking {
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
