package de.malteans.sosactivities

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.malteans.sosactivities.app.App
import de.malteans.sosactivities.di.initKoin
import org.jetbrains.compose.resources.painterResource
import sosactivities.composeapp.generated.resources.Res
import sosactivities.composeapp.generated.resources.ic_launcher_round

fun main() {
    application {
        initKoin()

        Window(
            onCloseRequest = ::exitApplication,
            title = "Hof Bockum Freizeitaktivitäten",
            icon = painterResource(Res.drawable.ic_launcher_round),
        ) {
            App()
        }
    }
}