package ru.astrainteractive.klibs.paging.context

/**
 * This is default implementation for [Int] page context
 *
 * @param page is a number of current page
 */
data class IntPageContext(val page: Int) : PageContext {
    /**
     * This is default factory for generating other contexts from previous
     */
    object Factory : PageContext.Factory<IntPageContext> {
        /**
         * When next called we will simply increment page number
         */
        override fun next(pageContext: IntPageContext): IntPageContext {
            return pageContext.copy(page = pageContext.page + 1)
        }
    }
}
