package ru.astrainteractive.klibs.paging.descriptor

data class IntPageDescriptor(val page: Int) : PageDescriptor<Int> {
    override fun next(): PageDescriptor<Int> {
        return copy(page = page + 1)
    }
}
