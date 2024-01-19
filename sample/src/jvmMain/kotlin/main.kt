@file:Suppress("Filename")

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.astrainteractive.klibs.sample.di.ServicesModule
import ru.astrainteractive.klibs.sample.feature.presentation.FeatureViewModel
import ru.astrainteractive.klibs.sample.feature.ui.FeatureApp
import java.awt.Dimension

fun main() = application {
    Window(
        title = "Multiplatform App",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        val servicesModule = ServicesModule.Default()
        ImageComposition {
            FeatureApp(
                featureViewModel = FeatureViewModel(
                    rickMortyRepository = servicesModule.rickMortyRepository
                )
            )
        }
    }
}
