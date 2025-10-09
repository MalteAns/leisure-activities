package de.malteans.sosactivities.core.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.malteans.sosactivities.themes.containerColor

@Composable
fun SettingsToggleItem(
    title: String,
    description: String? = null,
    state: Boolean,
    onStateChange: (Boolean) -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
        .clip(MaterialTheme.shapes.medium)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(
                if (onLongClick != null) Modifier
                    .combinedClickable(
                        enabled = enabled,
                        onClick = { onStateChange(!state) },
                        onLongClick = onLongClick,
                    )
                else Modifier
                    .clickable(enabled) { onStateChange(!state) }
            )
            .background(MaterialTheme.colorScheme.containerColor)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = state,
            onCheckedChange = onStateChange,
            enabled = enabled,
        )
    }
}