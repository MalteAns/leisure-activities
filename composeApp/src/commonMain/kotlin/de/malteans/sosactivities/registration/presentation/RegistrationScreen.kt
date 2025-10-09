package de.malteans.sosactivities.registration.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.sosactivities.core.presentation.util.SnackbarManager
import de.malteans.sosactivities.core.presentation.util.UiText
import de.malteans.sosactivities.core.presentation.util.currentPlattform
import de.malteans.sosactivities.model.ext.displayName
import de.malteans.sosactivities.registration.presentation.components.QrScannerScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sosactivities.composeapp.generated.resources.*

@Composable
fun RegistrationScreenRoot(
    onFinished: () -> Unit,
    viewModel: RegistrationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegistrationScreen(
        state = state,
        onAction = { action ->
            when (action) {
                RegistrationAction.OnUserCreated -> onFinished()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun RegistrationScreen(
    state: RegistrationState,
    onAction: (RegistrationAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            SnackbarManager.showThrowableSnackbar(error)

        }
    }

    LaunchedEffect(state.createdUser) {
        state.createdUser?.let { user ->
            onAction(RegistrationAction.OnUserCreated)
            SnackbarManager.showSnackbar(
                message = UiText.Resource(
                    Res.string.user_created_feedback,
                    arrayOf(user.displayName)
                ),
                withDismissAction = true,
            )
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    var showScanner by rememberSaveable { mutableStateOf(false) }
    if (showScanner) {
        QrScannerScreen(
            onScanned = { result ->
                if (result.length == 128 && result.all { it.isLetterOrDigit() }) {
                    showScanner = false
                    onAction(RegistrationAction.OnTokenChange(result))
                }
            },
            onClose = { showScanner = false }
        )
        return
    }

    var validInput by remember(state.firstName, state.lastName) {
        mutableStateOf(
            listOf(state.firstName, state.lastName).all { name ->
                name.isNotEmpty()
                && name.all { it.isValidNameChar() }
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
            .padding(horizontal = 32.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        OutlinedTextField(
            value = state.registrationToken,
            onValueChange = { newValue ->
                onAction(RegistrationAction.OnTokenChange(newValue))
            },
            label = { Text(stringResource(Res.string.registration_token)) },
            singleLine = true,
            maxLines = 1,
            enabled = state.validToken == false,
            supportingText = {
                // TODO: Add validation information
            },
            trailingIcon = {
                when {
                    state.validToken == true -> {
                        IconButton(onClick = {}, enabled = false) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Registration token is valid",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    state.validToken == null -> {
                        IconButton(onClick = { /* TODO: Implement cancel check */ }, enabled = false) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    state.registrationToken.isNotEmpty() -> {
                        IconButton(onClick = { onAction(RegistrationAction.OnTokenChange("")) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear registration token"
                            )
                        }
                    }
                    currentPlattform().isMobile -> {
                        IconButton(onClick = { showScanner = true }) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = stringResource(Res.string.scan_registration_token)
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Down)
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.firstName,
            onValueChange = { newValue ->
                onAction(RegistrationAction.OnFirstNameChange(newValue))
            },
            label = { Text(stringResource(Res.string.first_name)) },
            singleLine = true,
            maxLines = 1,
            enabled = state.validToken == true,
            isError = state.firstName.any { !it.isValidNameChar() },
            supportingText = {
                AnimatedVisibility(visible = state.firstName.any { !it.isValidNameChar() }) {
                    Text(
                        text = stringResource(Res.string.name_support_text),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Down)
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.lastName,
            onValueChange = { newValue ->
                onAction(RegistrationAction.OnLastNameChange(newValue))
            },
            label = { Text(stringResource(Res.string.last_name)) },
            singleLine = true,
            maxLines = 1,
            enabled = state.validToken == true,
            isError = state.lastName.any { !it.isValidNameChar() },
            supportingText = {
                AnimatedVisibility(visible = state.lastName.any { !it.isValidNameChar() }) {
                    Text(
                        text = stringResource(Res.string.name_support_text),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
                if (validInput) onAction(RegistrationAction.Submit)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { onAction(RegistrationAction.Submit) },
            enabled = validInput
        ) {
            Text(stringResource(Res.string.create_user) )
        }
    }
}

fun Char.isValidNameChar(): Boolean {
    return this.isLetter() || this.isWhitespace() || this == '-'
}