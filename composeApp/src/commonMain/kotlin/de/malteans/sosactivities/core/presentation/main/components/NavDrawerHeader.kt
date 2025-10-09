package de.malteans.sosactivities.core.presentation.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sosactivities.composeapp.generated.resources.Res
import sosactivities.composeapp.generated.resources.app_name
import sosactivities.composeapp.generated.resources.ic_launcher_round

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavDrawerHeader(
    modifier: Modifier = Modifier,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .height(42.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_launcher_round),
                    contentDescription = "Menu",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .scale(1.5f)
                )
            }
        }
        Column {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
            )
        }
    }
}