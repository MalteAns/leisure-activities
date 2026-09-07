package de.malteans.leisureactivities.core.presentation.components

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import leisureactivities.composeapp.generated.resources.Res
import leisureactivities.composeapp.generated.resources.ic_file_error
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ImageWithLoading(
    imageUrl: String?,
    contentDescription: String,
    width: Dp? = null,
    height: Dp? = 180.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    onFailureRes: DrawableResource = Res.drawable.ic_file_error,
    modifier: Modifier = Modifier,
) {
    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }
    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        onSuccess = {
            imageLoadResult =
                if (it.painter.intrinsicSize.width > 1 && it.painter.intrinsicSize.height > 1) {
                    Result.success(it.painter)
                } else {
                    Result.failure(Exception("Invalid image size"))
                }
        },
        onError = {
            it.result.throwable.printStackTrace()
            imageLoadResult = Result.failure(it.result.throwable)
        }
    )

    val painterState by painter.state.collectAsStateWithLifecycle()
    val animatedScale by animateFloatAsState(
        targetValue = if (painterState is AsyncImagePainter.State.Success) 0.4f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = EaseOutBack
        )
    )

    val sizeModifier = Modifier
        .then(
            if (width != null) Modifier.width(width)
            else Modifier.fillMaxWidth()
        )
        .then(
            if (height != null) Modifier.height(height)
            else Modifier.fillMaxHeight()
        )

    BoxWithConstraints (
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(sizeModifier)
    ) {
        when (val result = imageLoadResult) {
            null -> PulseAnimation(
                modifier = Modifier.size(
                    when {
                        width != null && height != null -> if (width < height) width else height
                        width != null -> width
                        height != null -> height
                        else -> this.maxWidth.coerceAtMost(this.maxHeight) * 0.5f
                    }
                )
            )

            else -> {
                Image(
                    painter = if (result.isSuccess) painter else {
                        painterResource(onFailureRes)
                    },
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .then(sizeModifier)
                        .graphicsLayer {
                            val scale = if (result.isSuccess) 0.6f + animatedScale else 1f
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(shape)
                )
            }
        }
    }
}