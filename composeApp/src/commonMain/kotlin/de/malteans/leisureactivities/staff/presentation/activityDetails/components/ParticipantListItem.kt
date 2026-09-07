package de.malteans.leisureactivities.staff.presentation.activityDetails.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import de.malteans.leisureactivities.model.Participant
import de.malteans.leisureactivities.themes.LeisureActivitiesTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ParticipantListItem(
    participant: Participant,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Person2,
                contentDescription = null,
            )
        },
//        overlineContent = {
//            Text(participant.userId)
//        },
        headlineContent = {
            Text("${participant.firstName} ${participant.lastName}")
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            headlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
    )
}

@Preview
@Composable
fun ParticipantListItemPreview() {
    LeisureActivitiesTheme {
        Surface {
            ParticipantListItem(
                participant = Participant(
                    userId = "userId",
                    firstName = "Max",
                    lastName = "Mustermann",
                ),
            )
        }
    }
}