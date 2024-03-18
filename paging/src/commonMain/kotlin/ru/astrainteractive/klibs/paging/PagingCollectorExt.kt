package ru.astrainteractive.klibs.paging

import ru.astrainteractive.klibs.paging.context.PageContext

/**
 * Some useful extensions for [IntPagerCollector]
 */
object PagingCollectorExt {

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

    suspend fun <T, K : PageContext> PagingCollector<T, K>.resetAndLoadNextPage() {
        resetAndJoin()
        loadNextPage()
    }
}
