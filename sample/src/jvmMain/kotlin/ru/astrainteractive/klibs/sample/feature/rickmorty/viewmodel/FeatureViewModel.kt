package ru.astrainteractive.klibs.sample.feature.rickmorty.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.astrainteractive.klibs.paging.state.model.PageResult
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyRepository
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.FeatureState
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Status
import kotlin.coroutines.CoroutineContext

internal class FeatureViewModel(private val rickMortyRepository: RickMortyRepository) : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate
    val model = rickMortyRepository.pagingState.map(
        transform = { pagingState ->
            FeatureState(
                characters = pagingState.items,
                filter = pagingState.pageContext.filter,
                isLastPage = pagingState.pageResult is PageResult.LastPage,
                isLoading = pagingState.pageResult is PageResult.Loading,
                isError = pagingState.pageResult is PageResult.Failure
            )
        }
    ).stateIn(
        scope = this,
        started = SharingStarted.Eagerly,
        initialValue = FeatureState(
            characters = emptyList(),
            filter = Filter(),
            isLastPage = false,
            isLoading = false,
            isError = false
        )
    )

    fun loadNextPage() {
        launch { rickMortyRepository.loadNextPage() }
    }

    fun onNameChanged(name: String) {
        rickMortyRepository.updateFilter(model.value.filter.copy(name = name))
    }

    fun onStatusChanged(status: Status) {
        rickMortyRepository.updateFilter(model.value.filter.copy(status = status))
    }

    fun reload() {
        launch {
            rickMortyRepository.reset()
            rickMortyRepository.loadNextPage()
        }
    }

    init {
        reload()
    }
}
