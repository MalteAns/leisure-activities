package de.malteans.sosactivities.app

import androidx.compose.runtime.Composable
import de.malteans.sosactivities.core.presentation.main.MainScreen
import de.malteans.sosactivities.themes.SosActivitiesTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    SosActivitiesTheme {
        MainScreen()
    }
}