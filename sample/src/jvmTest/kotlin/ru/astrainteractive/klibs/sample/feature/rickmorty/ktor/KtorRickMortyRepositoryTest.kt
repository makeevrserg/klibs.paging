@file:Suppress("FunctionNaming")

package ru.astrainteractive.klibs.sample.feature.rickmorty.ktor

import kotlinx.coroutines.test.runTest
import ru.astrainteractive.klibs.paging.state.PageResult
import ru.astrainteractive.klibs.sample.feature.rickmorty.fake.FakeRickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Status
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class KtorRickMortyRepositoryTest {
    @Test
    fun GIVEN_network_failure_WHEN_load_next_page_THEN_failure_is_exposed() = runTest {
        val expected = IllegalStateException("Network unavailable")
        val client = FakeRickMortyClient().apply {
            result = Result.failure(expected)
        }
        val repository = KtorRickMortyRepository(client)

        repository.loadNextPage()

        val pageResult = repository.pagingState.value.pageResult
        assertTrue(pageResult is PageResult.Failure)
        assertSame(expected, pageResult.throwable)
    }

    @Test
    fun GIVEN_cancellation_WHEN_load_next_page_THEN_cancellation_is_not_a_page_failure() = runTest {
        val client = FakeRickMortyClient().apply {
            result = Result.failure(CancellationException("Cancelled"))
        }
        val repository = KtorRickMortyRepository(client)

        repository.loadNextPage()

        assertFalse(repository.pagingState.value.pageResult is PageResult.Failure)
    }

    @Test
    fun GIVEN_successful_page_WHEN_load_next_page_THEN_items_and_context_are_updated() = runTest {
        val character = CharacterModel(
            id = 1,
            image = "https://example.com/rick.png",
            name = "Rick Sanchez",
            status = Status.ALIVE
        )
        val client = FakeRickMortyClient().apply {
            result = Result.success(listOf(character))
        }
        val repository = KtorRickMortyRepository(client)

        repository.loadNextPage()

        val pagingState = repository.pagingState.value
        assertContentEquals(listOf(character), pagingState.items)
        assertEquals(1, pagingState.pageContext.page)
        assertEquals(PageResult.Pending, pagingState.pageResult)
    }
}
