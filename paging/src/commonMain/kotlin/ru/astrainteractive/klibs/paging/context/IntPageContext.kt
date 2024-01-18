package ru.astrainteractive.klibs.paging.context

/**
 * This is default implementation for Integer
 */
data class IntPageContext(val page: Int) : PageContext {

    object Factory : PageContext.Factory<IntPageContext> {
        /**
         * When next called we will simply increment page number
         */
        override fun next(pageContext: IntPageContext): IntPageContext {
            return pageContext.copy(page = pageContext.page + 1)
        }
    }
}
