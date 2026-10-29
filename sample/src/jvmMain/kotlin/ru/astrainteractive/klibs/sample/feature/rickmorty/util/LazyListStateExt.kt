package ru.astrainteractive.klibs.sample.feature.rickmorty.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.filter

@Composable
internal fun LazyListState.OnEndReached(block: suspend () -> Unit) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo
                .lastOrNull()
                ?: return@derivedStateOf true
            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .filter { shouldLoad -> shouldLoad }
            .collect { block.invoke() }
    }
}
