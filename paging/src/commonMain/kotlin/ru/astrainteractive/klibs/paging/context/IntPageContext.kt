package ru.astrainteractive.klibs.paging.context

/**
 * This is default implementation for Integer
 */
data class IntPageContext(override val value: Int) : PageContext<Int> {
    /**
     * When next called we will simply increment page number
     */
    override fun next(): PageContext<Int> {
        return copy(value = value + 1)
    }
}
