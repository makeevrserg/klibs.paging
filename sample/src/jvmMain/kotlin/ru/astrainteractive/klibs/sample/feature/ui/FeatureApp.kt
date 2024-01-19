package ru.astrainteractive.klibs.sample.feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.Image
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.flow.filter
import ru.astrainteractive.klibs.sample.feature.presentation.FeatureViewModel
import ru.astrainteractive.klibs.sample.feature.service.model.Status

@Composable
fun LazyListState.OnEndReached(block: suspend () -> Unit) {
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
            .filter { it }
            .collect {
                if (it) block.invoke()
            }
    }
}

@Composable
@Suppress("LongMethod")
internal fun FeatureApp(featureViewModel: FeatureViewModel) {
    val model by featureViewModel.model.collectAsState()
    val lazyListState = rememberLazyListState()
    lazyListState.OnEndReached { featureViewModel.loadNextPage() }
    Scaffold {
        LazyColumn(state = lazyListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column {
                        OutlinedTextField(
                            value = model.filter.name.orEmpty(),
                            onValueChange = { featureViewModel.onNameChanged(it) },
                            label = { androidx.compose.material3.Text("Character name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                        Status.entries.forEach { status ->
                            Row {
                                RadioButton(
                                    selected = status == model.filter.status,
                                    onClick = { featureViewModel.onStatusChanged(status) }
                                )
                                Text(text = status.string)
                            }
                        }
                        FilledTonalButton(onClick = featureViewModel::reload) {
                            Text(text = "Reload")
                        }
                    }
                }
            }
            items(model.characters) {
                Card(modifier = Modifier.padding(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberImagePainter(it.image),
                            contentDescription = "image",
                            modifier = Modifier.size(64.dp)
                        )
                        Column {
                            Text("Character name: ${it.name}")
                            Text("Status: ${it.status.string}")
                        }
                    }
                }
            }
            item {
                when {
                    model.isLoading -> Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }

                    model.isError -> Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Oh no! Some error happened!")
                    }

                    model.isLastPage -> Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("You are on last page!")
                    }
                }
            }
        }
    }
}
