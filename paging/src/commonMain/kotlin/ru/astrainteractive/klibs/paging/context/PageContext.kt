package ru.astrainteractive.klibs.paging.context

/**
 * Represents a context for a specific page in a paginated data set.
 *
 * Implementations of this interface hold information about the current page,
 * such as its index, cursor, or other metadata needed to fetch the next or previous page.
 */
interface PageContext {

    /**
     * Factory interface for creating new instances of [PageContext], typically
     * representing the next or previous page in the pagination sequence.
     *
     * @param T The specific type of [PageContext] this factory produces.
     */
    interface Factory<T : PageContext> {

        /**
         * Creates a [PageContext] instance representing the next page based on the given [pageContext].
         *
         * @param pageContext The current page context.
         * @return A new [PageContext] representing the next page.
         */
        fun next(pageContext: T): T

        /**
         * Creates a [PageContext] instance representing the previous page based on the given [pageContext].
         *
         * @param pageContext The current page context.
         * @return A new [PageContext] representing the previous page.
         */
        fun prev(pageContext: T): T
    }
}
