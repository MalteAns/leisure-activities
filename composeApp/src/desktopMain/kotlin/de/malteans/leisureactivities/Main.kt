package de.malteans.leisureactivities

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.malteans.leisureactivities.app.App
import de.malteans.leisureactivities.di.initKoin
import leisureactivities.composeapp.generated.resources.Res
import leisureactivities.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() {
    application {
        initKoin()

        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
//            icon = painterResource(Res.drawable.ic_launcher_round),
        ) {
            App()
        }
    }
}