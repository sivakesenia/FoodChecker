package com.example.foodchecker.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodchecker.ui.main.MainRoute
import com.example.foodchecker.ui.profile.ProfileRoute

object Destinations {
    const val MAIN = "main"
    const val PROFILE = "profile"
}

private const val TRANSITION_DURATION_MS = 350

@Composable
fun FoodCheckerNavHost(
    widthSizeClass: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.MAIN,
        enterTransition = {
            slideIntoContainer(SlideDirection.Start, tween(TRANSITION_DURATION_MS)) +
                fadeIn(tween(TRANSITION_DURATION_MS))
        },
        exitTransition = {
            slideOutOfContainer(SlideDirection.Start, tween(TRANSITION_DURATION_MS)) +
                fadeOut(tween(TRANSITION_DURATION_MS))
        },
        popEnterTransition = {
            slideIntoContainer(SlideDirection.End, tween(TRANSITION_DURATION_MS)) +
                fadeIn(tween(TRANSITION_DURATION_MS))
        },
        popExitTransition = {
            slideOutOfContainer(SlideDirection.End, tween(TRANSITION_DURATION_MS)) +
                fadeOut(tween(TRANSITION_DURATION_MS))
        }
    ) {
        composable(Destinations.MAIN) {
            MainRoute(
                widthSizeClass = widthSizeClass,
                onOpenProfile = dropUnlessResumed {
                    navController.navigate(Destinations.PROFILE) { launchSingleTop = true }
                }
            )
        }
        composable(Destinations.PROFILE) {
            ProfileRoute(
                onBack = dropUnlessResumed { navController.popBackStack() }
            )
        }
    }
}
