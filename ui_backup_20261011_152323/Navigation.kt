package com.example.rentmate

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainRentMateNavigation() {

    val context = LocalContext.current

    val restored = remember {
        RentMateSessionStore.restore(context)
    }

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val mainTabs = listOf(
        "dashboard",
        "accommodation_details",
        "activity",
        "profile"
    )

    fun navigateToTab(route: String) {
        if (route == currentRoute) return

        navController.navigate(route) {
            popUpTo("dashboard") {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun logout() {
        RentMateSessionStore.clear(context)

        navController.navigate("login") {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in mainTabs) {
                RentMateBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navigateToTab(route)
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (restored) {
                "dashboard"
            } else {
                "welcome"
            },
            modifier = Modifier.padding(innerPadding)
        ) {

            // ==========================================
            // WELCOME
            // ==========================================

            composable("welcome") {

                WelcomeScreen(
                    onGetStartedClick = {
                        navController.navigate("signup")
                    },
                    onLoginClick = {
                        navController.navigate("login")
                    }
                )
            }

            // ==========================================
            // SIGN UP
            // ==========================================

            composable("signup") {

                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate("login") {
                            popUpTo("signup") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onLoginClick = {
                        navController.navigate("login") {
                            popUpTo("signup") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ==========================================
            // LOGIN
            // ==========================================

            composable("login") {

                LoginScreen(
                    onLoginSuccess = {

                        if (!RentMateSession.accessToken.isNullOrBlank()) {

                            RentMateSessionStore.save(context)

                            navController.navigate("dashboard") {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate("signup")
                    }
                )
            }

            // ==========================================
            // DASHBOARD
            // ==========================================

            composable("dashboard") {

                DashboardScreen(
                    onProfileClick = {
                        navigateToTab("profile")
                    },
                    onSettingsClick = {
                        navController.navigate("settings")
                    },
                    onLogoutClick = {
                        logout()
                    },
                    onFindAccommodationClick = {
                        navigateToTab("accommodation_details")
                    },
                    onFeatureNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ==========================================
            // EXPLORE
            // ==========================================

            composable("accommodation_details") {

                AccommodationPageScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onRequestJoin = {
                        // Backend integration comes later.
                    }
                )
            }

            // ==========================================
            // ACTIVITY
            // ==========================================

            composable("activity") {

                RentMateActivityScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ==========================================
            // PROFILE
            // ==========================================

            composable("profile") {

                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogoutClick = {
                        logout()
                    }
                )
            }

            // ==========================================
            // SETTINGS
            // ==========================================

            composable("settings") {

                SettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogoutClick = {
                        logout()
                    }
                )
            }

            // ==========================================
            // CHORES
            // ==========================================

            composable("chores") {

                ChoresScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ==========================================
            // GROCERIES
            // ==========================================

            composable("groceries") {

                GroceriesScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ==========================================
            // KITCHEN BOOKINGS
            // ==========================================

            composable("household_events") {
                HouseholdEventsScreen(onBack = { navController.popBackStack() })
            }

            composable("kitchen_bookings") {

                KitchenBookingScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
