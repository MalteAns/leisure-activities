package de.malteans.sosactivities.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.malteans.sosactivities.core.domain.DataStoreRepository
import de.malteans.sosactivities.core.presentation.settings.SettingsScreenRoot
import de.malteans.sosactivities.registration.presentation.RegistrationScreenRoot
import de.malteans.sosactivities.signUp.presentation.SignUpOverviewScreenRoot
import de.malteans.sosactivities.staff.presentation.activityDetails.ActivityDetailsScreenRoot
import de.malteans.sosactivities.staff.presentation.modifyActivity.ModifyActivityScreenRoot
import de.malteans.sosactivities.staff.presentation.overview.StaffOverviewScreenRoot
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    showDrawer: (Boolean) -> Unit,
    setCurScreen: (CurScreen) -> Unit,
) {
    val dataStoreRepository = koinInject<DataStoreRepository>()

    NavHost(
        navController = navController,
        startDestination = if (dataStoreRepository.isUserCreated()) Route.SignUpNav
            else Route.MainNav,
        // Set default transitions to None (individual composables below override)
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        navigation<Route.MainNav>(
            startDestination = Route.Main.Register
        ) {
            composable<Route.Main.Register> {
                setCurScreen(CurScreen.REGISTER)
                RegistrationScreenRoot(
                    onFinished = {
                        navController.navigateWithPop<Route.MainNav>(Route.SignUpNav)
                    }
                )
            }
            composable<Route.Main.Settings> {
                setCurScreen(CurScreen.SETTINGS)
                SettingsScreenRoot(
                    showDrawer = showDrawer
                )
            }
        }
        navigation<Route.SignUpNav>(
            startDestination = Route.SignUp.Overview
        ) {
            composable<Route.SignUp.Overview> {
                setCurScreen(CurScreen.SIGN_UP)
                SignUpOverviewScreenRoot(
                    showDrawer = showDrawer
                )
            }
        }
        navigation<Route.StaffNav>(
            startDestination = Route.Staff.Overview
        ) {
            composable<Route.Staff.Overview> {
                setCurScreen(CurScreen.STAFF_AREA)
                StaffOverviewScreenRoot(
                    showDrawer = showDrawer,
                    onModifyActivity = { activityId ->
                        navController.navigate(Route.Staff.ModifyActivity(activityId))
                    },
                    onShowActivity = { activityId ->
                        navController.navigate(Route.Staff.ActivityDetails(activityId))
                    },
                )
            }
            composable<Route.Staff.ActivityDetails>(
                enterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } },
                popEnterTransition = { EnterTransition.None }
            ) { backStackEntry ->
                val activityId = backStackEntry.toRoute<Route.Staff.ActivityDetails>().activityId
                ActivityDetailsScreenRoot(
                    activityId = activityId,
                    onEditActivity = { navController.navigate(Route.Staff.ModifyActivity(activityId)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<Route.Staff.ModifyActivity>(
                enterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } },
            ) { backStackEntry ->
                val activityId = backStackEntry.toRoute<Route.Staff.ModifyActivity>().activityId
                ModifyActivityScreenRoot(
                    activityId = activityId,
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

inline fun <reified T: Route> NavController.navigateWithPop(route: Route) {
    this.navigate(route) {
        popUpTo(T::class) {
            inclusive = true
        }
    }
}

@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}