package ru.astrainteractive.klibs.paging.state.model

import ru.astrainteractive.klibs.paging.context.model.PageContext

/**
 * Represents the complete state of a paginated data flow.
 *
 * @param T The type of items being paginated.
 * @param K The type of [PageContext] representing the current paging position.
 *
 * @property pageContext The current context of the page, used to determine paging direction.
 * @property items The list of all loaded items up to this point.
 * @property pageResult The result of the most recent page loading operation.
 */
public data class PagingState<T, K : PageContext>(
    public val pageContext: K,
    public val items: List<T>,
    public val pageResult: PageResult
)
