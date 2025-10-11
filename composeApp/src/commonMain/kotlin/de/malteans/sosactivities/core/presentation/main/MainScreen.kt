package de.malteans.sosactivities.core.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import de.malteans.sosactivities.core.domain.MainService
import de.malteans.sosactivities.core.presentation.main.components.CustomNavigationDrawer
import de.malteans.sosactivities.core.presentation.main.components.NavDrawerSheet
import de.malteans.sosactivities.core.presentation.util.SnackbarManager
import de.malteans.sosactivities.navigation.CurScreen
import de.malteans.sosactivities.navigation.NavGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MainScreen() {
    val mainService = koinInject<MainService>()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarManager.snackbarMessages.collect { snackbarValue ->
            val snackbarResult = snackbarHostState.showSnackbar(
                message = snackbarValue.message,
                actionLabel = snackbarValue.actionLabel,
                withDismissAction = snackbarValue.withDismissAction,
                duration = snackbarValue.duration
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                snackbarValue.onAction()
            }
        }
    }

    var userIsStaff by remember { mutableStateOf(mainService.getUserIsStaff()) }

    LaunchedEffect(Unit) {
        if (mainService.isUserCreated()) {
            mainService.refreshToken()
                .onSuccess { userIsStaff = it }
        }
    }

    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var curScreen by rememberSaveable { mutableStateOf(
        if (mainService.isUserCreated()) CurScreen.SIGN_UP
        else CurScreen.REGISTER
    ) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(curScreen) {
        if (curScreen == CurScreen.REGISTER) drawerState.close()
    }

    Scaffold (
        snackbarHost = { SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 48.dp)
        ) },
        modifier = Modifier.fillMaxSize(),
    ) {
        CustomNavigationDrawer(
            drawerState = drawerState,
            visible = curScreen != CurScreen.REGISTER,
            drawerContent = {
                NavDrawerSheet(
                    navController = navController,
                    curScreen = curScreen,
                    closeDrawer = { scope.launch(Dispatchers.IO) { drawerState.close() } },
                    isStaff = userIsStaff
                )
            },
        ) {
            NavGraph(
                navController = navController,
                showDrawer = { show ->
                    scope.launch(Dispatchers.IO) {
                        if (show) drawerState.open()
                        else drawerState.close()
                    }
                },
                setCurScreen = { curScreen = it },
            )
        }
    }
}