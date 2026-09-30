package com.example.sinope.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.sinope.presentation.addaccount.AddAccountScreen
import com.example.sinope.presentation.editAccount.EditAccountScreen
import com.example.sinope.presentation.home.HomeScreen
import com.example.sinope.presentation.settings.language.LanguageScreen
import com.example.sinope.presentation.onboarding.OnboardingScreen
import com.example.sinope.presentation.onboarding.viewmodel.OnBoardingViewModel
import com.example.sinope.presentation.onboarding.viewmodel.UiEvent
import com.example.sinope.presentation.importaccounts.ImportAccountsScreen
import com.example.sinope.presentation.settings.SettingsScreen


@Composable
fun NavGraph(
    startDestination:String,
) {

    val navController = rememberNavController()

    /// EntryNavigationApp
    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.OnBoardingScreen.route
        ){
            composable(route = Route.OnBoardingScreen.route) {
                val viewModel: OnBoardingViewModel = hiltViewModel()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->

                        when(event){
                            UiEvent.NavigateToHome -> {
                                navController.navigate(Route.MainNavigation.route){
                                    popUpTo(Route.AppStartNavigation.route){
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                }

                OnboardingScreen(onEvent = viewModel::onEvent)
            }
        }

        /// MainNavigationApp
        navigation(
            route = Route.MainNavigation.route,
            startDestination = Route.HomeScreen.route
        ){
            composable(route = Route.HomeScreen.route) {
                HomeScreen(
                    onAddAccount = {
                        navController.navigate(Route.AddAccountScreen.route)
                    },
                    onEditAccount = { accountId ->
                        navController.navigate(Route.EditAccountScreen.withId(accountId))
                    },
                    onOpenSettings = {
                        navController.navigate(Route.SettingsScreen.route)
                    }
                )
            }

            composable(route=Route.AddAccountScreen.route){
                AddAccountScreen(
                    onAccountAdded = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Route.EditAccountScreen.route,
                arguments = Route.EditAccountScreen.arguments
            ) {
                EditAccountScreen(
                    onBack = { navController.popBackStack() },
                    onDeleted = { navController.popBackStack() },
                    onSave = { navController.popBackStack()}
                )
            }

            composable(
                route = Route.SettingsScreen.route
            ) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLanguageChange = { navController.navigate(Route.LanguageScreen.route)},
                    onImportAccounts = { navController.navigate(Route.ImportAccountsScreen.route) }
                )
            }

            composable(
                route= Route.LanguageScreen.route
            ) {
                LanguageScreen(
                    onBack = { navController.popBackStack()}
                )
            }

            composable(
                route = Route.ImportAccountsScreen.route
            ) {
                ImportAccountsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

}



































