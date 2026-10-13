package com.example.foodchecker.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodchecker.ui.details.ProductDetailsRoute
import com.example.foodchecker.ui.main.MainRoute
import com.example.foodchecker.ui.profile.ProfileRoute
import com.example.foodchecker.ui.scanner.ScannerRoute

object Destinations {
    const val MAIN = "main"
    const val PROFILE = "profile"
    const val SCANNER = "scanner"
    const val BARCODE_ARG = "barcode"
    const val PRODUCT = "product/{$BARCODE_ARG}"

    fun product(barcode: String): String = "product/$barcode"
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
                },
                onOpenScanner = dropUnlessResumed {
                    navController.navigate(Destinations.SCANNER) { launchSingleTop = true }
                },
                onOpenProduct = { barcode ->
                    navController.navigate(Destinations.product(barcode)) { launchSingleTop = true }
                }
            )
        }
        composable(Destinations.PROFILE) {
            ProfileRoute(
                onBack = dropUnlessResumed { navController.popBackStack() }
            )
        }
        composable(Destinations.SCANNER) {
            ScannerRoute(
                widthSizeClass = widthSizeClass,
                onBack = dropUnlessResumed { navController.popBackStack() },
                onProductFound = { barcode ->
                    navController.navigate(Destinations.product(barcode)) {
                        popUpTo(Destinations.SCANNER) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Destinations.PRODUCT,
            arguments = listOf(navArgument(Destinations.BARCODE_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString(Destinations.BARCODE_ARG).orEmpty()
            ProductDetailsRoute(
                barcode = barcode,
                widthSizeClass = widthSizeClass,
                onBack = dropUnlessResumed { navController.popBackStack() }
            )
        }
    }
}
