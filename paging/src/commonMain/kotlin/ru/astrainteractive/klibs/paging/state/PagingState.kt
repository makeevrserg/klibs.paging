package ru.astrainteractive.klibs.paging.state

import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.context.PageContext

/**
 * This is a state of your paging
 *
 * [K] is the context of your page - can be [IntPageContext]; string whatever
 *
 * @see PageContext
 * @param pageContext Context of current page
 * @param items Retrieved items from your data source
 */
data class PagingState<T, K : PageContext>(
    val pageContext: K,
    val items: List<T>,
    val pageResult: PageResult
)
