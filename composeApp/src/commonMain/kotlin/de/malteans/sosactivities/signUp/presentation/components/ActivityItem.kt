package de.malteans.sosactivities.signUp.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import de.malteans.sosactivities.core.presentation.util.UiText
import de.malteans.sosactivities.core.presentation.util.toDateTimeString
import de.malteans.sosactivities.core.presentation.util.toTimeString
import de.malteans.sosactivities.core.presentation.util.toUiTexts
import de.malteans.sosactivities.model.ActivityWithImageUrl
import de.malteans.sosactivities.themes.SosActivitiesTheme
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sosactivities.composeapp.generated.resources.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(FormatStringsInDatetimeFormats::class, ExperimentalTime::class)
@Composable
fun ActivityItem(
    activity: ActivityWithImageUrl,
    onButtonClicked: () -> Unit,
    onTTS: ((List<UiText>) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showExtendedInfo by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        enabled = onTTS != null,
        onClick = {
            onTTS?.invoke(activity.toUiTexts())
        },
        modifier = modifier.fillMaxWidth()
    ) {
        // top image if available
        if (activity.imageUrl != null) { // TODO: Implement loading animation
            AsyncImage(
                model = activity.imageUrl,
                contentDescription = activity.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = activity.startsAt.toDateTimeString() + (activity.endsAt?.let { " - ${it.toTimeString()}" } ?: ""),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            TextButton(
                onClick = { showExtendedInfo = !showExtendedInfo },
                contentPadding = PaddingValues(0.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(
                            if (showExtendedInfo) Res.string.hide_details
                            else Res.string.show_details
                        )
                    )
                    Icon(
                        imageVector = if (showExtendedInfo) Icons.Default.ExpandLess
                            else Icons.Default.ExpandMore,
                        contentDescription = null,
                    )
                }
            }
            AnimatedVisibility(
                visible = showExtendedInfo,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    verticalArrangement = spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    listOf<Pair<StringResource, String>>(
                        Pair(Res.string.meet_up_details_title, activity.meetUpInformation),
                        Pair(Res.string.location_details_title, activity.activityLocation),
                        Pair(Res.string.host_details_title, activity.hostInformation),
                        Pair(Res.string.contact_person_details_title, activity.contactPersonInformation),
                    ).takeIf { pairs -> pairs.any { it.second.isNotBlank() } }?.forEach { (title, text) ->
                        if (text.isNotBlank()) {
                            DetailsInfoItem(
                                title = stringResource(title),
                                text = text,
                            )
                        }
                    } ?: Text(stringResource(Res.string.no_additional_information_available))
                }
            }
            ElevatedButton(
                onClick = onButtonClicked,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (activity.signedUp == true) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.primary,
                    contentColor = if (activity.signedUp == true) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                enabled = activity.signedUp != null
            ) {
                when (activity.signedUp) {
                    false -> Text(stringResource(Res.string.sign_up))
                    true -> Text(stringResource(Res.string.signed_up))
                    null -> CircularProgressIndicator(
                        color = LocalContentColor.current,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun ActivityItemPreview() {
    SosActivitiesTheme {
        ActivityItem(
            activity = ActivityWithImageUrl(
                id = "1",
                title = "Schlittschuhlaufen",
                imageId = "1506744038136-46273834b3fb",
                imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
                startsAt = Instant.parse("2025-10-13T15:20:00Z"),
                endsAt = Instant.parse("2025-10-13T18:30:00Z"),
                meetUpInformation = "17:20 Abfahrt in Bockum, 17:25 Haltestelle Kirche, 17:30 Heidehaus, 18:00 Schwimmbad/Eisstadion",
                activityLocation = "Walter-Maack Eisstadion Scharnebecker Weg, 21365 Adendorf",
                hostInformation = "FSJler*innen",
                contactPersonInformation = "Sozialdienst Monika Beutel",
                signedUp = true,
            ),
            onButtonClicked = {}
        )
    }
}

