package de.malteans.leisureactivities.app

import androidx.compose.runtime.Composable
import de.malteans.leisureactivities.core.presentation.main.MainScreen
import de.malteans.leisureactivities.themes.LeisureActivitiesTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    LeisureActivitiesTheme {
        MainScreen()
    }
}