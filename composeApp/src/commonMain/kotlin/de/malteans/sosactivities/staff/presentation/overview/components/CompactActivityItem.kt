package de.malteans.sosactivities.staff.presentation.overview.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import de.malteans.sosactivities.core.presentation.util.toDateTimeString
import de.malteans.sosactivities.core.presentation.util.toTimeString
import de.malteans.sosactivities.model.ActivityWithImageUrl
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CompactActivityItem(
    activity: ActivityWithImageUrl,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text(activity.title) },
        supportingContent = { Text(activity.startsAt.toDateTimeString() + (activity.endsAt?.let { " - ${it.toTimeString()}" } ?: "")) },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
            supportingColor = MaterialTheme.colorScheme.onSecondaryContainer,
            trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
    )
}