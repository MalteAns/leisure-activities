package de.malteans.sosactivities.staff.presentation.modifyActivity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.malteans.sosactivities.model.Image
import leisureactivities.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImagePickerDialog(
    allImages: List<Image>,
    onUploadImage: (PickedImageData) -> Unit,
    uploadInProgress: Boolean,
    onDismissRequest: () -> Unit,
    onImagePicked: (Image) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            ImagePicketContent(
                allImages = allImages,
                onUploadImage = onUploadImage,
                uploadInProgress = uploadInProgress,
                onDismissRequest = onDismissRequest,
                onImagePicked = onImagePicked
            )
        },
    )

    /*
    if (currentPlatform().isMobile) {
        val scope = rememberCoroutineScope()

        // Animatable for the sheet height fraction.
        val sheetHeightAnimatable = remember { Animatable(0f) }

        fun customBackHandling() {
            scope.launch {
                sheetHeightAnimatable.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 250, easing = EaseInOut)
                )
                onDismissRequest()
            }
        }
        BackHandler { customBackHandling() }

        // Startup animation: animate from 0f to the initial minSheetFraction over 800ms.
        LaunchedEffect(Unit) {
            sheetHeightAnimatable.animateTo(
                targetValue = 0.8f,
                animationSpec = tween(durationMillis = 350, easing = EaseInOut)
            )
        }

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface.copy(
                    alpha = sheetHeightAnimatable.value * 0.5f
                ))
                .pointerInput(Unit) {
                    /* Catch taps outside the sheet */
                }
                .fillMaxSize()
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(sheetHeightAnimatable.value)
                    .align(Alignment.BottomCenter)
            ) {
                ImagePicketContent(
                    allImages = allImages,
                    onUploadImage = onUploadImage,
                    onDismissRequest = onDismissRequest,
                    onImagePicked = onImagePicked
                )
            }
        }
    }
     */
}

@Composable
private fun ImagePicketContent(
    allImages: List<Image>,
    onUploadImage: (PickedImageData) -> Unit,
    uploadInProgress: Boolean,
    onDismissRequest: () -> Unit,
    onImagePicked: (Image) -> Unit,
) {
    var pickedImageData by remember { mutableStateOf<PickedImageData?>(null) }

    val imagePickerLauncher = rememberImagePickerLauncher { imageData ->
        pickedImageData = imageData
    }
    var isHoveringWithFile by remember {
        mutableStateOf(false)
    }
    val dragAndDropTarget = rememberDragAndDropTarget(
        onHover = { isHovered ->
            isHoveringWithFile = isHovered
        },
        onDrop = { imageData ->
            pickedImageData = imageData
        }
    )

    pickedImageData?.let { data ->
        var filename by remember { mutableStateOf(data.filename.substringBeforeLast(".")) }
        AlertDialog(
            onDismissRequest = { pickedImageData = null },
            title = { Text(stringResource(Res.string.upload_image_dialog_title)) },
            text = {
                Column {
                    Text(stringResource(Res.string.upload_image_dialog_desc1))
                    OutlinedTextField(
                        value = filename,
                        onValueChange = { filename = it },
                        placeholder = { Text(stringResource(Res.string.filename)) },
                        suffix = data.mimeType.substringAfterLast('/').let { { Text(".$it") } },
                        isError = !filename.isValidFilename(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(stringResource(Res.string.upload_image_dialog_desc2))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUploadImage(data.copy(filename = "$filename.${data.mimeType.substringAfterLast('/')}"))
                        pickedImageData = null
                    },
                    enabled = filename.isValidFilename(),
                ) {
                    Text(stringResource(Res.string.upload))
                }
            },
            dismissButton = {
                TextButton({ pickedImageData = null }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.large
                )
                .padding(12.dp)
                .heightIn(max = 450.dp)
                .fillMaxWidth()
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { true },
                    target = dragAndDropTarget
                )
        ) {
            Button(
                onClick = { imagePickerLauncher.launch() },
                enabled = !uploadInProgress,
            ) {
                Text(stringResource(Res.string.upload_image))
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                verticalArrangement = spacedBy(4.dp),
                horizontalArrangement = spacedBy(4.dp),
            ) {
                items(allImages.size) { index ->
                    val image = allImages[index]
                    ImageItem(
                        image = image,
                        onClick = {
                            onImagePicked(image)
                            onDismissRequest()
                        },
                    )
                }
            }
        }
        if (uploadInProgress) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        if (isHoveringWithFile) {
            DragAndDropOverlay(
                modifier = Modifier
                    .matchParentSize()
                    .align(Alignment.Center)
            )
        }
    }
}

private fun String.isValidFilename(): Boolean {
    // Simple check: filename should not be blank and should not contain slashes
    return this.isNotBlank() && this.all { it.isLetterOrDigit() || it in listOf('_', '-') }
}