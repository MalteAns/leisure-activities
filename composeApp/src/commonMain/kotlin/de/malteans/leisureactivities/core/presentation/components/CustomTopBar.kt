package de.malteans.leisureactivities.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.malteans.leisureactivities.core.presentation.util.currentPlatform
import leisureactivities.composeapp.generated.resources.Res
import leisureactivities.composeapp.generated.resources.menu
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    subtitle: String? = null,
    onOpenDrawer: (() -> Unit)? = null,
    navigationIcon: @Composable (RowScope.() -> Unit) = onOpenDrawer?.let { openDrawer ->
        {
            IconButton(openDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(Res.string.menu),
                )
            }
        }
    } ?: {},
    actions: @Composable (RowScope.() -> Unit) = {},
    modifier: Modifier = Modifier
) {
    if (currentPlatform().isMobile || onOpenDrawer == null) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            navigationIcon = { Row { navigationIcon.invoke(this) } },
            actions = { actions.invoke(this) },
            modifier = modifier
                .clip(
                    shape = if (currentPlatform().isMobile) MaterialTheme.shapes.extraLarge.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                    ) else RectangleShape
                ),
        )
    }
}