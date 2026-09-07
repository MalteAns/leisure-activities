package de.malteans.leisureactivities.core.presentation.util

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class SnackbarValue(
    val message: String,
    val actionLabel: String?,
    val withDismissAction: Boolean,
    val duration: SnackbarDuration,
    val onAction: (() -> Unit),
)

object SnackbarManager {
    private val _snackbarMessages = MutableSharedFlow<SnackbarValue>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()

    suspend fun showSnackbar(
        message: UiText, actionLabel: String? = null,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        withDismissAction: Boolean = duration == SnackbarDuration.Indefinite,
        onAction: () -> Unit = {},
    ) {
        _snackbarMessages.emit(SnackbarValue(message.asStringAsync(), actionLabel, withDismissAction, duration, onAction))
    }

    suspend fun showThrowableSnackbar(
        throwable: Throwable
    ) {
        showSnackbar(
            message = throwable.toUiText(),
            withDismissAction = true,
            duration = SnackbarDuration.Short,
        )
    }
}
