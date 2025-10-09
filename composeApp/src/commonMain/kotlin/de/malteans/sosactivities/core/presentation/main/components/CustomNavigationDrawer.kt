package de.malteans.sosactivities.core.presentation.main.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.malteans.sosactivities.core.presentation.util.currentPlattform

/**
 * @param[drawerState] Only for mobile (modal) Drawer
 * @param[gesturesEnabled] Only for mobile (modal) Drawer
 * @param[scrimColor] Only for mobile (modal) Drawer
 * */
@Composable
fun CustomNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit
) {
    if (currentPlattform().isMobile) {
        ModalNavigationDrawer(
            drawerContent = drawerContent,
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            scrimColor = scrimColor,
            content = content
        )
    } else {
        PermanentNavigationDrawer(
            drawerContent = drawerContent,
            modifier = modifier,
            content = content
        )
    }
}