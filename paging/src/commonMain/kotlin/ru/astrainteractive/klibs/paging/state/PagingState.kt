package ru.astrainteractive.klibs.paging.state

import ru.astrainteractive.klibs.paging.context.PageContext

/**
 * Core paging state
 * [K] is the type of page - can be int; string whatever
 *
 * @see PageContext
 */
data class PagingState<T, K : PageContext>(
    val pageContext: K,
    val items: List<T>,
    val pageSizeAtLeast: Int,
    val isLastPage: Boolean,
    val isLoading: Boolean,
    val isFailure: Boolean
)
