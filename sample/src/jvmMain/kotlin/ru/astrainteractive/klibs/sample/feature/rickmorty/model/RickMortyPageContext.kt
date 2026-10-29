package ru.astrainteractive.klibs.sample.feature.rickmorty.model

import ru.astrainteractive.klibs.paging.context.PageContext

internal data class RickMortyPageContext(
    val page: Int,
    val filter: Filter
) : PageContext {
    object Factory : PageContext.Factory<RickMortyPageContext> {
        override fun next(pageContext: RickMortyPageContext): RickMortyPageContext {
            return pageContext.copy(page = pageContext.page + 1)
        }

        override fun prev(pageContext: RickMortyPageContext): RickMortyPageContext {
            return pageContext.copy(page = pageContext.page - 1)
        }
    }
}
