package de.malteans.leisureactivities.core.presentation.main.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import de.malteans.leisureactivities.navigation.CurScreen
import de.malteans.leisureactivities.navigation.Route
import leisureactivities.composeapp.generated.resources.Res
import leisureactivities.composeapp.generated.resources.settings
import leisureactivities.composeapp.generated.resources.sign_up_overview
import leisureactivities.composeapp.generated.resources.staff_area
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavDrawerSheet(
    navController: NavController,
    curScreen: CurScreen,
    closeDrawer: () -> Unit,
    isStaff: Boolean,
) {
    ModalDrawerSheet {
        NavDrawerHeader()
        NavigationDrawerItem(
            label = { Text(stringResource(Res.string.sign_up_overview)) },
            onClick = {
                navController.navigate(Route.SignUpNav)
                closeDrawer()
            },
            selected = curScreen == CurScreen.SIGN_UP,
            icon = {
                Icon(
                    imageVector = if (curScreen == CurScreen.SIGN_UP) Icons.Filled.AppRegistration
                        else Icons.Outlined.AppRegistration,
                    contentDescription = stringResource(Res.string.sign_up_overview)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        if (isStaff) {
            NavigationDrawerItem(
                label = { Text(stringResource(Res.string.staff_area)) },
                onClick = {
                    navController.navigate(Route.StaffNav)
                    closeDrawer()
                },
                selected = curScreen == CurScreen.STAFF_AREA,
                icon = {
                    Icon(
                        imageVector = if (curScreen == CurScreen.STAFF_AREA) Icons.Filled.ManageAccounts
                            else Icons.Outlined.ManageAccounts,
                        contentDescription = stringResource(Res.string.staff_area)
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }
        Spacer(Modifier.weight(1f))
        NavigationDrawerItem(
            label = { Text(stringResource(Res.string.settings)) },
            onClick = {
                navController.navigate(Route.Main.Settings)
                closeDrawer()
            },
            selected = curScreen == CurScreen.SETTINGS,
            icon = {
                Icon(
                    imageVector = if (curScreen == CurScreen.SETTINGS) Icons.Filled.Settings
                        else Icons.Outlined.Settings,
                    contentDescription = stringResource(Res.string.settings)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
    }
}