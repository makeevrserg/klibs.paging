package ru.astrainteractive.klibs.paging.descriptor

/**
 * [PageDescriptor] is the description of your current page.
 *
 * It can be anything, but mostly page described as number, [Int] for example
 */
interface PageDescriptor<T : Any> {
    /**
     * Descriptor value
     *
     * In case when it's [IntPageDescriptor] it will be page number
     */
    val value: T

    /**
     * Create next [PageDescriptor]
     */
    fun next(): PageDescriptor<T>
}
