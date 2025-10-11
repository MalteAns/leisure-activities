package de.malteans.sosactivities.staff.presentation.modifyActivity.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ModifyActivityTextField(
    newValue: String?,
    loadedValue: String?,
    onValueChange: ((String) -> Unit)?,
    labelRes: StringResource,
    lines: Int = 1,
    trailingContent: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    ModifyActivityTextField(
        newValue = newValue,
        loadedValue = loadedValue,
        onValueChange = onValueChange,
        labelRes = labelRes,
        lines = Pair(lines, lines),
        trailingContent = trailingContent,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
    )
}

@Composable
fun ModifyActivityTextField(
    newValue: String?,
    loadedValue: String?,
    onValueChange: ((String) -> Unit)?,
    labelRes: StringResource,
    lines: Pair<Int, Int>,
    trailingContent: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    OutlinedTextField(
        value = newValue ?: loadedValue ?: "" ,
        enabled = onValueChange != null,
        onValueChange = onValueChange ?: {},
        label = { Text(stringResource(labelRes)) },
        minLines = lines.first,
        maxLines = lines.second,
        trailingIcon = trailingContent,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = OutlinedTextFieldDefaults.colors().let { if (isError) it.errorContainerColor else it.unfocusedContainerColor },
            disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            disabledLabelColor = OutlinedTextFieldDefaults.colors().let { if (isError) it.errorLabelColor else it.unfocusedLabelColor },
            disabledTextColor = OutlinedTextFieldDefaults.colors().let { if (isError) it.errorTextColor else it.unfocusedTextColor },
            disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().let { if (isError) it.errorTrailingIconColor else it.unfocusedTrailingIconColor },
        ).let {
            if (newValue != null) it.copy(
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                disabledLabelColor = MaterialTheme.colorScheme.primary,
            ) else it
        },
        modifier = modifier
    )
}