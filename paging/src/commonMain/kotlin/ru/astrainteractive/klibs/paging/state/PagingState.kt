package ru.astrainteractive.klibs.paging.state

import ru.astrainteractive.klibs.paging.descriptor.PageDescriptor

/**
 * Core paging state
 * [T] is the type of page - can be int; string whatever
 */
data class PagingState<T : Any>(
    val pageDescriptor: PageDescriptor<T>,
    val pageSizeAtLeast: Int,
    val isLastPage: Boolean,
    val isLoading: Boolean,
    val isFailure: Boolean
)
