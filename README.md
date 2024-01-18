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

### Define your own PageContext

PageContext is the description of your current page. It can be int, string, anything.
Most users describe page as Integer value, so here's example:

```kotlin
data class LongPageContext(override val value: Long) : PageContext<Long> {
    override fun next(): PageContext<Long> {
        return copy(value = value + 1)
    }
}
```

### Create PagedDataSource

Mostly you will use LambdaPagedListDataSource, but it also can be created with PagedListDataSource interface

```kotlin
// With interface
class BytesPagedListDataSource : PagedListDataSource<Byte, Long> {
    override suspend fun getListResult(pagingState: PagingState<Long>): Result<List<Byte>> {
        return runCatching { listOf(0.toByte()) }
    }
}
// Or lambda
val bytesPagedListDataSource = LambdaPagedListDataSource<Byte, Long> {
    runCatching { listOf(0.toByte()) }
}
```

### Define your own paging collector

For custom PagingDescriptor you need to create PagerCollector. It can be made with delegation

```kotlin
class LongPagerCollector<T>(
    private val initialPage: Long = 0L,
    private val pageSize: Int = 10,
    private val pager: PagedListDataSource<T, Long>,
) : PagingCollector<T, Long> by DefaultPagingCollector(
    initialPagingState = PagingState(
        pageContext = LongPageContext(value = initialPage),
        pageSizeAtLeast = pageSize,
        isLastPage = false,
        isLoading = false,
        isFailure = false
    ),
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
    private val pagingCollector = LongPagerCollector(
        initialPage = 0,
        pager = LambdaPagedListDataSource { pagingState ->
            runCatching {
                loadPage(
                    page = pagingState.pageContext.value,
                    pageSize = pagingState.pageSizeAtLeast
                )
            }
        }
    )

    private suspend fun loadPage(page: Long, pageSize: Int): List<String> {
        return if (Random.nextBoolean()) List(pageSize) { i -> "Value number ${pageSize * page} + $i" }
        else error("Some error")
    }

    /**
     * Define stateflow of your pager
     */
    override val pagingStateFlow: StateFlow<PagingState<Long>> = pagingCollector.pagingStateFlow

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

