package ru.astrainteractive.klibs.paging.context

/**
 * [PageContext] is the description of your current page.
 *
 * It can be anything, but mostly page described as number, [Int] for example
 */
interface PageContext<T : Any> {
    /**
     * Context value
     *
     * In case when it's [IntPageContext] it will be page number
     */
    val value: T

    /**
     * Create next [PageContext]
     */
    fun next(): PageContext<T>
}
