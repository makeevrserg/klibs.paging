package ru.astrainteractive.klibs.paging.util

import kotlinx.coroutines.flow.first
import ru.astrainteractive.klibs.paging.collector.PagingCollector
import ru.astrainteractive.klibs.paging.context.PageContext

/**
 * Force submit page context
 */
fun <T, K : PageContext> PagingCollector<T, K>.submitPageContext(pageContext: K) {
    update { pagingState ->
        pagingState.copy(pageContext = pageContext)
    }
}

/**
 * Force submit page context
 */
fun <T, K : PageContext> PagingCollector<T, K>.updatePageContext(pageContext: (K) -> K) {
    val nextPageContext = pageContext.invoke(state.value.pageContext)
    submitPageContext(nextPageContext)
}

/**
 * Update current items
 */
fun <T, K : PageContext> PagingCollector<T, K>.submitList(list: List<T>) {
    update { it.copy(items = list) }
}

suspend fun <T, K : PageContext> PagingCollector<T, K>.loadNextPage() {
    loadPage(nextContext = { pageContextFactory -> pageContextFactory.next(state.first().pageContext) })
}

suspend fun <T, K : PageContext> PagingCollector<T, K>.loadPreviousPage() {
    loadPage(nextContext = { pageContextFactory -> pageContextFactory.prev(state.first().pageContext) })
}

suspend fun <T, K : PageContext> PagingCollector<T, K>.resetAndLoadNextPage() {
    cancelAndJoin()
    resetToInitial()
    loadNextPage()
}
