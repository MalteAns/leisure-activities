package de.malteans.sosactivities.core.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.sosactivities.core.presentation.components.CustomTopBar
import de.malteans.sosactivities.core.presentation.settings.components.InformationTextDialog
import de.malteans.sosactivities.core.presentation.settings.components.SettingsToggleItem
import de.malteans.sosactivities.core.presentation.util.UiText
import de.malteans.sosactivities.core.presentation.util.currentPlatform
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sosactivities.composeapp.generated.resources.*

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    showDrawer: (Boolean) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is SettingsAction.ShowDrawer -> showDrawer(action.show)
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
        }
    }
}