package de.malteans.leisureactivities.staff.presentation.modifyActivity.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.malteans.leisureactivities.core.presentation.components.ImageWithLoading
import de.malteans.leisureactivities.model.Image
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ImageItem(
    image: Image,
    onClick: () -> Unit,
) {
    ImageWithLoading(
        imageUrl = image.publicUrl,
        contentDescription = image.filename,
        width = null,
        height = null,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    )
}