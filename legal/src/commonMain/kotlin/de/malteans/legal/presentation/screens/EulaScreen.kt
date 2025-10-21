package de.malteans.legal.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import leisureactivities.legal.generated.resources.Res
import leisureactivities.legal.generated.resources.eula_title
import leisureactivities.legal.generated.resources.navigate_back
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EulaScreen(
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.eula_title)) },
                navigationIcon = {
                    IconButton(navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

    }
}