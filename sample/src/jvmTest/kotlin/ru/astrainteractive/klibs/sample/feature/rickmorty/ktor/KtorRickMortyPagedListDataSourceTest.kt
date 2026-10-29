@file:Suppress("FunctionNaming")

package ru.astrainteractive.klibs.sample.feature.rickmorty.ktor

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.klibs.paging.state.model.PageResult
import ru.astrainteractive.klibs.paging.state.model.PagingState
import ru.astrainteractive.klibs.sample.feature.rickmorty.fake.FakeRickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyCharacter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyPageContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class KtorRickMortyPagedListDataSourceTest {
    @Test
    fun GIVEN_cancellation_WHEN_get_list_result_THEN_cancellation_is_rethrown(): TestResult {
        return runTest {
            val expected = CancellationException("Cancelled")
            val client = FakeRickMortyClient().apply {
                result = Result.failure(expected)
            }
            val dataSource = KtorRickMortyPagedListDataSource(client)
            val pagingState = PagingState(
                pageContext = RickMortyPageContext(page = 0, filter = Filter()),
                items = emptyList<RickMortyCharacter>(),
                pageResult = PageResult.Pending
            )

            val actual = assertFailsWith<CancellationException> {
                dataSource.getListResult(pagingState)
            }

            assertSame(expected, actual)
        }
    }
}
