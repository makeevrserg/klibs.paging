@file:Suppress("Filename", "KindShape")

package ru.astrainteractive.klibs.sample

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.astrainteractive.klibs.sample.composable.ImageComposition
import ru.astrainteractive.klibs.sample.di.RootModule
import ru.astrainteractive.klibs.sample.feature.rickmorty.composable.screen.FeatureApp
import ru.astrainteractive.klibs.sample.feature.rickmorty.viewmodel.FeatureViewModel
import java.awt.Dimension

@Suppress("MagicNumber")
fun main() {
    application {
        Window(
            title = "Multiplatform App",
            state = rememberWindowState(width = 800.dp, height = 600.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(350, 600)
            val rootModule = RootModule()
            ImageComposition {
                FeatureApp(
                    featureViewModel = FeatureViewModel(
                        rickMortyRepository = rootModule.rickMortyModule.rickMortyRepository
                    )
                )
            }
        }
    }
}
