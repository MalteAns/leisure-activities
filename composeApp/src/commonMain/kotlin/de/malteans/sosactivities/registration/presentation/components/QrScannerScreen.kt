package de.malteans.sosactivities.registration.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import de.malteans.sosactivities.core.presentation.util.SnackbarManager
import de.malteans.sosactivities.core.presentation.util.UiText
import kotlinx.coroutines.launch
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun QrScannerScreen(
    onScanned: (String) -> Unit,
    onClose: () -> Unit,
) {
    var flashlightOn by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                QrScanner(
                    modifier = Modifier
                        .clipToBounds()
                        .clip(shape = RoundedCornerShape(size = 14.dp)),
                    flashlightOn = flashlightOn,
                    openImagePicker = false,
                    onCompletion = onScanned,
                    imagePickerHandler = {},
                    onFailure = {
                        coroutineScope.launch {
                            if (it.isEmpty()) {
                                SnackbarManager.showSnackbar(UiText.DynamicString("Invalid qr code"))
                            } else {
                                SnackbarManager.showSnackbar(UiText.DynamicString(it))
                            }
                        }
                    },
                    cameraLens = CameraLens.Back,
                )
            }
        }

        Row(
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QRScanner",
                modifier = Modifier.weight(1f),
            )

            IconButton(onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                )
            }
        }
    }
}