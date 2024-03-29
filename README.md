[![version](https://img.shields.io/maven-central/v/ru.astrainteractive.klibs/paging?style=flat-square)](https://github.com/makeevrserg/klibs.paging)
[![kotlin_version](https://img.shields.io/badge/kotlin-1.9.0-blueviolet?style=flat-square)](https://github.com/makeevrserg/klibs.paging)

## Paging

klibs.Paging is kotlin-only lightweight paging library

See also other klibs libraries

- [kdi](https://github.com/makeevrserg/klibs.kdi)
- [kstorage](https://github.com/makeevrserg/klibs.kstorage)
- [mikro](https://github.com/makeevrserg/klibs.mikro)

## Installation

Gradle

```kotlin
implementation("ru.astrainteractive.klibs:paging:<version>")
implementation(libs.klibs.paging)
```

Version catalogs

```toml
[versions]
klibs-paging = "<latest-version>"

[libraries]
klibs-paging = { module = "ru.astrainteractive.klibs:paging", version.ref = "klibs-paging" }
```

## Sample

For sample see [Sample directory](./sample)

It contains ComposeJB RickMortyApi paging implementation with filtering

## Issues with usage

If you have issues with usage of this library - take a look into sources. Almost everything documented and designed to be easy to understand

## Usage

### Define your own PageContext

PageContext is the description of your current page. It can contain int, string, anything.
Most users describe page as Integer value, so here's example:

Factory here is used to create next or previous page context

```kotlin
data class LongPageContext(val page: Long) : PageContext {
    object Factory : PageContext.Factory<LongPageContext> {
        override fun next(pageContext: LongPageContext): LongPageContext {
            return pageContext.copy(page = pageContext.page + 1)
        }
    }
}
```

### Create PagedDataSource

Mostly you will use LambdaPagedListDataSource, but it also can be created with PagedListDataSource interface

```kotlin
// With interface
class BytesPagedListDataSource : PagedListDataSource<Byte, LongPageContext> {
    override suspend fun getListResult(pagingState: PagingState<Byte, LongPageContext>): Result<List<Byte>> {
        return runCatching { listOf(0.toByte()) }
    }
}

// Or lambda
val bytesPagedListDataSource = LambdaPagedListDataSource<Byte, LongPageContext> {
    runCatching { listOf(0.toByte()) }
}
```

### Define your own paging collector

For custom PageContext you need to create PagerCollector. It can be made with delegation

```kotlin
class LongPagerCollector<T>(
    private val initialPage: Long = 0L,
    private val pageSize: Int = 10,
    private val pager: PagedListDataSource<T, LongPageContext>,
) : PagingCollector<T, LongPageContext> by DefaultPagingCollector(
    initialPagingStateFactory = {
        PagingState(
            pageContext = LongPageContext(page = initialPage),
            items = emptyList(),
            pageSizeAtLeast = pageSize,
            isLastPage = false,
            isLoading = false,
            isFailure = false
        )
    },
    pager = pager,
    pageContextFactory = LongPageContext.Factory
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
        pager = CoroutineHandledPagedListDataSource<String, LongPageContext> { pagingState ->
            val page = pagingState.pageContext.page
            val pageSize = pagingState.pageSizeAtLeast
            if (Random.nextBoolean()) List(pagingState.pageContext.page.toInt()) { i -> "Value number ${pageSize * page} + $i" }
            else error("Some error")
        }
    )

    /**
     * Define stateflow of your pager
     */
    override val pagingState: StateFlow<PagingState<String, LongPageContext>> = pagingCollector.state

    /**
     * Add ability to reset it
     */
    override suspend fun reset() {
        pagingCollector.resetAndJoin()
    }

    /**
     * Add ability to load next page
     */
    override suspend fun loadNextPage() {
        pagingCollector.loadNextPage()
    }
}
```

