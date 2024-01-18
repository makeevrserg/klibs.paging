package ru.astrainteractive.klibs.paging.state

import ru.astrainteractive.klibs.paging.context.PageContext

/**
 * Core paging state
 * [T] is the type of page - can be int; string whatever
 *
 * @see PageContext
 */
data class PagingState<T : PageContext>(
    val pageContext: T,
    val pageSizeAtLeast: Int,
    val isLastPage: Boolean,
    val isLoading: Boolean,
    val isFailure: Boolean
)
