package com.kloc.unistore.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kloc.unistore.navigation.Screen.SchoolDetailsScreen
import com.kloc.unistore.screens.ProductDetailScreen
import com.kloc.unistore.screens.SchoolDetailsScreen
import com.kloc.unistore.screens.SchoolCategoryScreen
import com.kloc.unistore.util.Constants.CATEGORY_ID
import com.kloc.unistore.util.Constants.SCHOOL_ID

@Composable
@ExperimentalComposeUiApi
fun NavGraph(
    navController: NavHostController
) {

    NavHost(navController = navController, startDestination =SchoolDetailsScreen.route) {
        composable(route = SchoolDetailsScreen.route)
        {
          SchoolDetailsScreen(navController = navController)
        }
        composable(
            route = Screen.SchoolCategoryScreen.route,
            arguments = listOf(navArgument(SCHOOL_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val schoolId = backStackEntry.arguments?.getInt(SCHOOL_ID)
            if (schoolId != null) {
                SchoolCategoryScreen(navController = navController, schoolId = schoolId)
            }
        }

        composable(
            route = Screen.ProductDetailsScreen.route,
            arguments = listOf(navArgument(CATEGORY_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt(CATEGORY_ID)
            if (categoryId != null) {
                ProductDetailScreen(navController = navController, categoryId = categoryId)
            }
        }

    }
}






