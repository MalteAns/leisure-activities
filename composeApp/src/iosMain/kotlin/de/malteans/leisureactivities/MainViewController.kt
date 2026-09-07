package de.malteans.leisureactivities

import androidx.compose.ui.window.ComposeUIViewController
import de.malteans.leisureactivities.app.App
import de.malteans.leisureactivities.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}