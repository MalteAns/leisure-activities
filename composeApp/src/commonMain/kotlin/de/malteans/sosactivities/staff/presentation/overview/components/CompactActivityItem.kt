package de.malteans.sosactivities.staff.presentation.overview.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import de.malteans.sosactivities.core.presentation.util.toDateTimeString
import de.malteans.sosactivities.core.presentation.util.toTimeString
import de.malteans.sosactivities.model.ActivityWithImageUrl
import leisureactivities.composeapp.generated.resources.Res
import leisureactivities.composeapp.generated.resources.edit
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CompactActivityItem(
    activity: ActivityWithImageUrl,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    showCheckBox: Boolean = false,
    checkBoxValue: Boolean = false,
    onCheckBoxValueChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val indication = LocalIndication.current
    val sharedInteractionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = showCheckBox,
            enter = expandHorizontally(),
            exit = shrinkHorizontally(),
        ) {
            Checkbox(
                checked = checkBoxValue,
                onCheckedChange = onCheckBoxValueChange,
                interactionSource = sharedInteractionSource,
            )
        }
        ListItem(
            headlineContent = {
                Text(
                    text = activity.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            supportingContent = {
                Text(
                    text = activity.startsAt.toDateTimeString() + (activity.endsAt?.let { " - ${it.toTimeString()}" } ?: ""),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    onEditClick?.let { onEditClick ->
                        FilledIconButton(
                            onClick = onEditClick,
                            enabled = !showCheckBox,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(Res.string.edit),
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                supportingColor = MaterialTheme.colorScheme.onSecondaryContainer,
                trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .weight(1f)
                .then(
                    if (showCheckBox) {
                        Modifier
                            .indication(sharedInteractionSource, indication)
                            .clickable(
                                onClick = { onCheckBoxValueChange(!checkBoxValue) },
                                interactionSource = sharedInteractionSource,
                                indication = null,
                            )
                    }
                    else if (onLongClick != null) {
                        Modifier
                            .indication(sharedInteractionSource, indication)
                            .combinedClickable(
                                onClick = onClick,
                                onLongClick = onLongClick,
                                interactionSource = sharedInteractionSource,
                                indication = null,
                            )
                    } else {
                        Modifier
                            .indication(sharedInteractionSource, indication)
                            .clickable(
                                onClick = onClick,
                                interactionSource = sharedInteractionSource,
                                indication = null,
                            )
                    }
                )
        )
    }
}