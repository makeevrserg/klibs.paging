package ru.astrainteractive.klibs.paging.descriptor

/**
 * This is default implementation for Integer
 */
data class IntPageDescriptor(override val value: Int) : PageDescriptor<Int> {
    /**
     * When next called we will simply increment page number
     */
    override fun next(): PageDescriptor<Int> {
        return copy(value = value + 1)
    }
}
