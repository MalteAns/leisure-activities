package de.malteans.leisureactivities.core.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.legal.presentation.components.LegalsList
import de.malteans.legal.presentation.navigation.LegalRoute
import de.malteans.leisureactivities.core.presentation.components.CustomTopBar
import de.malteans.leisureactivities.core.presentation.settings.components.InformationTextDialog
import de.malteans.leisureactivities.core.presentation.settings.components.SettingsToggleItem
import de.malteans.leisureactivities.core.presentation.util.UiText
import de.malteans.leisureactivities.core.presentation.util.currentPlatform
import de.malteans.leisureactivities.themes.containerColor
import leisureactivities.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    showDrawer: (Boolean) -> Unit,
    navigateToLegalScreen: (LegalRoute) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is SettingsAction.ShowDrawer -> showDrawer(action.show)
                is SettingsAction.NavigateToLegalScreen -> navigateToLegalScreen(action.route)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    var informationTextToShow by remember { mutableStateOf<UiText?>(null) }
    informationTextToShow?.let { text ->
        InformationTextDialog(
            text = text,
            onDismiss = { informationTextToShow = null },
        )
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Res.string.settings),
                onOpenDrawer = { onAction(SettingsAction.ShowDrawer(true)) }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { pad ->
        Column(
            Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp)
                .padding(pad)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsToggleItem(
                title = stringResource(Res.string.tts_on_tap),
                description = stringResource(Res.string.tts_on_tap_desc),
                state = state.ttsEnabled,
                onStateChange = { onAction(SettingsAction.TtsEnabledChange(it)) },
                onLongClick = {
                    informationTextToShow = UiText.Resource(Res.string.tts_on_tap_adv_desc)
                },
                enabled = currentPlatform().isMobile,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            LegalsList(
                tileContainerColor = MaterialTheme.colorScheme.containerColor,
                navigateToLegalScreen = { route ->
                    onAction(SettingsAction.NavigateToLegalScreen(route))
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
            )
        }
    }
}