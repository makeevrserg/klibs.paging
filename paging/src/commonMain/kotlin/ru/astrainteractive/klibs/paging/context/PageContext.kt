package ru.astrainteractive.klibs.paging.context

/**
 * [PageContext] is the description of your current page.
 *
 * It can be anything, but mostly page described as number, [Int] for example
 */
interface PageContext {
    /**
     * Factory is required to generate next or previous page context
     *
     * @see IntPageContext.Factory
     */
    interface Factory<T : PageContext> {
        fun next(pageContext: T): T
    }
}
