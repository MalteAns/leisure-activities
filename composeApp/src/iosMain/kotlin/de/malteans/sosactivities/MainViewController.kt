package de.malteans.sosactivities

import androidx.compose.ui.window.ComposeUIViewController
import de.malteans.sosactivities.app.App
import de.malteans.sosactivities.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}