package de.malteans.sosactivities.signUp.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import de.malteans.sosactivities.model.ActivityWithImageUrl
import org.jetbrains.compose.resources.stringResource
import sosactivities.composeapp.generated.resources.*

@Composable
fun SignOutDialog(
    activity: ActivityWithImageUrl,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.sign_out_dialog_title)) },
        text = { Text(stringResource(Res.string.sign_out_dialog_desc, activity.title)) },
        confirmButton = {
            TextButton(onConfirm) {
                Text(stringResource(Res.string.sign_out_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}