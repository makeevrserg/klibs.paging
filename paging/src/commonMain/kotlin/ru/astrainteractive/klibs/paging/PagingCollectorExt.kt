package ru.astrainteractive.klibs.paging

import ru.astrainteractive.klibs.paging.context.PageContext

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
        val nextPageContext = pageContext.invoke(pagingStateFlow.value.pageContext)
        submitPageContext(nextPageContext)
    }
}
