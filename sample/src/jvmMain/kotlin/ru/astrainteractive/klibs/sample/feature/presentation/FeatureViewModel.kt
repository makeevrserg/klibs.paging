package ru.astrainteractive.klibs.sample.feature.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.astrainteractive.klibs.sample.feature.data.RickMortyRepository
import ru.astrainteractive.klibs.sample.feature.service.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.service.model.Filter
import ru.astrainteractive.klibs.sample.feature.service.model.Status
import kotlin.coroutines.CoroutineContext

internal class FeatureViewModel(private val rickMortyRepository: RickMortyRepository) : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate
    val model = rickMortyRepository.pagingState.map(
        transform = { pagingState ->
            Model(
                characters = pagingState.items,
                filter = pagingState.pageContext.filter,
                isLastPage = pagingState.isLastPage,
                isLoading = pagingState.isLoading,
                isError = pagingState.isFailure
            )
        }
    ).stateIn(this, SharingStarted.Eagerly, Model())

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

    class Model(
        val characters: List<CharacterModel> = emptyList(),
        val filter: Filter = Filter(),
        val isLastPage: Boolean = false,
        val isLoading: Boolean = false,
        val isError: Boolean = false
    )
}
