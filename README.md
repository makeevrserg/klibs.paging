[![version](https://img.shields.io/maven-central/v/ru.astrainteractive.klibs/paging?style=flat-square)](https://github.com/makeevrserg/klibs.paging)
[![kotlin_version](https://img.shields.io/badge/kotlin-1.9.0-blueviolet?style=flat-square)](https://github.com/makeevrserg/klibs.paging)

## Paging

klibs.Paging is kotlin-only lightweight paging library

## Installation

Gradle

```kotlin
implementation("ru.astrainteractive.klibs:paging:<version>")
```

Version catalogs

```toml
[versions]
klibs-paging = "<latest-version>"

[libraries]
klibs-paging = { module = "ru.astrainteractive.klibs:paging", version.ref = "klibs-paging" }
```

### Define your own PagingState

```kotlin
data class LongPagingState(
    override var pageDescriptor: Int,
    override var isLastPage: Boolean = false,
    override val isLoading: Boolean = false,
    override val isFailure: Boolean = false
) : PagingState<Long> {
    override fun createNextPageDescriptor(): Int {
        return pageDescriptor + 1
    }

    override fun copyPagingState(
        pageDescriptor: Int,
        isLastPage: Boolean,
        isLoading: Boolean,
        isFailure: Boolean
    ): PagingState<Int> {
        return this.copy(
            pageDescriptor = pageDescriptor,
            isLastPage = isLastPage,
            isLoading = isLoading,
            isFailure = isFailure
        )
    }
}
```

### Define your own paging collector

```kotlin

class LongPagerCollector<T>(
    private val initialPage: Long = 0,
    private val pager: PagedListDataSource<T, Int>,
) : PagingCollector<T, Int> by DefaultPagingCollector(
    initialPagingState = LongPagingState(pageDescriptor = initialPage),
    pager = pager
)
```

### Simple repository example

```kotlin
/**
 * Your custom repository implementation
 */
class MyRepositoryImpl : MyRepository {
    /**
     * Define your paging collector
     */
    private val pagingCollector = IntPagerCollector(
        initialPage = 0,
        pager = LambdaPagedListDataSource { pagingState ->
            val page = pagingState.pageDescriptor
            val pageSize = 10
            runCatching {
                if (Random.nextBoolean()) List(pageSize) { i ->
                    "Value number ${pageSize * page} + $i"
                }
                else error("Some error")

            }
        }
    )

    /**
     * Define stateflow of your pager
     */
    override val pagingStateFlow: StateFlow<PagingState<Int>> = pagingCollector.pagingStateFlow

    /**
     * Define list state flow of your pager
     */
    override val pagingItems: StateFlow<List<String>> = pagingCollector.listStateFlow

    /**
     * Add ability to reset it
     */
    override suspend fun reset() {
        pagingCollector.reset()
    }

    /**
     * Add ability to load next page
     */
    override suspend fun loadNextPage() {
        pagingCollector.loadNextPage()
    }
}
```

