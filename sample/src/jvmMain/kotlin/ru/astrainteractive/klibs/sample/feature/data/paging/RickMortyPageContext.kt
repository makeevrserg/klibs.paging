package ru.astrainteractive.klibs.sample.feature.data.paging

import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.sample.feature.service.model.Filter

internal data class RickMortyPageContext(
    val page: Int,
    val filter: Filter
) : PageContext {
    object Factory : PageContext.Factory<RickMortyPageContext> {
        override fun next(pageContext: RickMortyPageContext): RickMortyPageContext {
            return pageContext.copy(page = pageContext.page + 1)
        }
    }
}
