package ru.astrainteractive.klibs.paging.descriptor

interface PageDescriptor<T : Any> {
    fun next(): PageDescriptor<T>
}
