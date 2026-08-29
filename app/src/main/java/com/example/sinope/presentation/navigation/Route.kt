package com.example.sinope.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(
    val route:String,
    val arguments: List<NamedNavArgument> = emptyList()
) {


    // Graphs
    data object AppStartNavigation : Route("app_start_navigation")
    data object MainNavigation : Route("main_navigation")

    // Screens
    data object OnBoardingScreen : Route("onboarding")
    data object HomeScreen : Route("home")
    data object AddAccountScreen : Route("add_account_screen")
    data object SettingsScreen : Route("settings")
    data object LanguageScreen: Route("language")

    data object EditAccountScreen : Route(
        route = "edit_account_screen/{$ACCOUNT_ID_ARG}",
        arguments = listOf(
            navArgument(ACCOUNT_ID_ARG) { type = NavType.StringType }
        )
    ) {
        /** Concrete route for [accountId], e.g. `edit_account_screen/7`. */
        fun withId(accountId: String): String = "edit_account_screen/$accountId"
    }

    companion object {
        const val ACCOUNT_ID_ARG = "accountId"
    }
}