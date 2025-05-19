package ru.astrainteractive.klibs.paging.util

import kotlinx.coroutines.flow.first
import ru.astrainteractive.klibs.paging.collector.PagingCollector
import ru.astrainteractive.klibs.paging.context.PageContext

/**
 * Replaces the current [PageContext] in the [PagingState] with the specified [pageContext].
 *
 * @param pageContext The new page context to set.
 */
fun <T, K : PageContext> PagingCollector<T, K>.submitPageContext(pageContext: K) {
    update { pagingState ->
        pagingState.copy(pageContext = pageContext)
    }
}

/**
 * Applies a transformation to the current [PageContext] and updates it in the [PagingState].
 *
 * @param pageContext A function that takes the current page context and returns an updated one.
 */
fun <T, K : PageContext> PagingCollector<T, K>.updatePageContext(pageContext: (K) -> K) {
    val nextPageContext = pageContext.invoke(state.value.pageContext)
    submitPageContext(nextPageContext)
}

/**
 * Replaces the current list of items in the [PagingState] with the given [list].
 *
 * @param list The new list of items to submit.
 */
fun <T, K : PageContext> PagingCollector<T, K>.submitList(list: List<T>) {
    update { it.copy(items = list) }
}

/**
 * Loads the next page using the current [PageContext] and the factory's `next()` method.
 */
suspend fun <T, K : PageContext> PagingCollector<T, K>.loadNextPage() {
    loadPage(nextContext = { pageContextFactory -> pageContextFactory.next(state.first().pageContext) })
}

/**
 * Loads the previous page using the current [PageContext] and the factory's `prev()` method.
 */
suspend fun <T, K : PageContext> PagingCollector<T, K>.loadPreviousPage() {
    loadPage(nextContext = { pageContextFactory -> pageContextFactory.prev(state.first().pageContext) })
}

/**
 * Cancels any ongoing work, resets the paging state to its initial value, and loads the next page.
 */
suspend fun <T, K : PageContext> PagingCollector<T, K>.resetAndLoadNextPage() {
    cancelAndJoin()
    resetToInitial()
    loadNextPage()
}
