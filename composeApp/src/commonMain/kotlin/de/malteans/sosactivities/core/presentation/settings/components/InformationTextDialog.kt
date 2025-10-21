package de.malteans.sosactivities.core.presentation.settings.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.malteans.sosactivities.core.presentation.util.UiText
import leisureactivities.composeapp.generated.resources.Res
import leisureactivities.composeapp.generated.resources.close
import org.jetbrains.compose.resources.stringResource

@Composable
fun InformationTextDialog(
    text: UiText,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            OutlinedButton(onDismiss) {
                Text(stringResource(Res.string.close))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
            )
        },
        text = {
            Text(text.asString())
        }
    )
}