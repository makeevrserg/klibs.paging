package ru.astrainteractive.klibs.paging.context

/**
 * A simple [PageContext] implementation that uses an integer-based page index.
 *
 * @property page The index of the current page.
 */
data class IntPageContext(val page: Int) : PageContext {

    /**
     * Factory for creating the next or previous [IntPageContext] instances by incrementing or
     * decrementing the current page index.
     */
    object Factory : PageContext.Factory<IntPageContext> {

        /**
         * Returns the [IntPageContext] representing the next page by incrementing the current page index.
         *
         * @param pageContext The current [IntPageContext].
         * @return A new [IntPageContext] with the page index incremented by 1.
         */
        override fun next(pageContext: IntPageContext): IntPageContext {
            return pageContext.copy(page = pageContext.page + 1)
        }

        /**
         * Returns the [IntPageContext] representing the previous page by decrementing the current page index.
         *
         * @param pageContext The current [IntPageContext].
         * @return A new [IntPageContext] with the page index decremented by 1.
         */
        override fun prev(pageContext: IntPageContext): IntPageContext {
            return pageContext.copy(page = pageContext.page - 1)
        }
    }
}
