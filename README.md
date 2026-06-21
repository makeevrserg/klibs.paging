[![version](https://img.shields.io/maven-central/v/ru.astrainteractive.klibs/paging?style=flat-square)](https://github.com/makeevrserg/klibs.paging)

### 📦 What is it?

`klibs.paging` — lightweight Kotlin-only library simplifies pagination in your Kotlin Multiplatform projects. Whether
you're building for Android, iOS, JVM, or JS, `klibs.paging` provides a clean and efficient way to handle paginated
data.

### 🚀 Features

- **Multiplatform Support:** Works seamlessly across Android, iOS, JVM, and JS.
- **Lightweight:** Minimal dependencies and a small footprint.
- **Easy Integration:** Simple setup with Gradle.
- **Flexible API:** Supports both manual and automatic pagination strategies.

### 🧪 Sample Usage

For a practical example, check out the sample directory in the repository. It demonstrates how to implement pagination
in a Kotlin Multiplatform project.

### 🧩 Core Concepts

- **PagingSource<Key, Value>** — defines how to load pages of data from your source (API, DB, etc).
- **Pager** — coordinates paging, calls your PagingSource to load pages as needed.
- **PagingState** — tracks loaded pages and current position.
- **LoadResult** — success or failure result of a page load.
- **Page** — a chunk of loaded data with info about previous and next keys.

### 🎯 Use Cases

- Loading infinite scroll lists from paginated REST APIs.
- Paging database query results.
- Any scenario where loading entire data set at once is inefficient or impossible.

### 🚀 Getting Started

#### Installation

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

#### Define your Paging Context

```kotlin
data class IntPageContext(val page: Int) : PageContext {
    object Factory : PageContext.Factory<IntPageContext> {

        override fun next(pageContext: IntPageContext): IntPageContext {
            return pageContext.copy(page = pageContext.page + 1)
        }

        override fun prev(pageContext: IntPageContext): IntPageContext {
            return pageContext.copy(page = pageContext.page - 1)
        }
    }
}

```

#### Defina a Collector

```kotlin
class IntPagerCollector<T>(
    private val initialPage: Int = 0,
    private val pager: PagedListDataSource<T, IntPageContext>,
) : PagingCollector<T, IntPageContext> by DefaultPagingCollector(
    initialPagingStateFactory = {
        PagingState(
            pageContext = IntPageContext(page = initialPage),
            items = emptyList(),
            pageResult = PageResult.Pending
        )
    },
    pager = pager,
    pageContextFactory = IntPageContext.Factory
)
```

#### Collect Pages!

```kotlin
private val pagingCollector = IntPagerCollector(
    initialPage = 0,
    pager = {
        runCatching {
            listOf("Hello first item!")
        }
    }
)

val state = pagingCollector.pagingState

fun loadItem() {
    pagingCollector.loadNextPage()
}
```

---

## 💜 Support Us

If our projects help you, consider supporting their development.

<table>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/bitcoin/F7931A" width="25" alt="BTC"/><br/>
<sub><b>Bitcoin</b></sub>
</td>
<td>

```text
bc1q9a8dr55jgfae0mhevw3vvczegjv0khfp0ngrnv
```

</td>
</tr>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/ethereum/627EEA" width="25" alt="ETH"/><br/>
<sub><b>Ethereum</b></sub>
</td>
<td>

```text
0x0BaAeEA44Ce08c8DC139224ff57563695B30d423
```

</td>
</tr>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/boosty/F15F2C" width="25" alt="Boosty"/><br/>
<sub><b>Boosty</b></sub>
</td>
<td align="center">
<a href="https://boosty.to/empireprojekt/donate">
<img width="70%" src="https://img.shields.io/badge/Donate-Boosty-F15F2C?style=for-the-badge&logo=boosty&logoColor=white" alt="Donate on Boosty"/>
</a>
</td>
</tr>
</table>

---