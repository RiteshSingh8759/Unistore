package com.kloc.unistore.navigation


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen.SchoolDetailsScreen
import com.kloc.unistore.screens.CartScreen
import com.kloc.unistore.screens.OrderDetailsScreen
import com.kloc.unistore.screens.ProductDetailScreen
import com.kloc.unistore.screens.ProductScreen
import com.kloc.unistore.screens.SchoolDetailsScreen
import com.kloc.unistore.screens.SchoolCategoryScreen
import com.kloc.unistore.screens.StudentDetailsScreen
import com.kloc.unistore.util.Constants.CATEGORY_ID
import com.kloc.unistore.util.Constants.PRODUCT_ID
import com.kloc.unistore.util.Constants.SCHOOL_ID

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@ExperimentalComposeUiApi
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    // Observe the current route in the back stack
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unipro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Show the person icon only on the OrderDetailsScreen
                    if (currentRoute == Screen.OrderDetailsScreen.route) {
                        IconButton(onClick = { navController.navigate(Screen.StudentDetailsScreen.route) }) {
                            Icon(Icons.Default.Person, contentDescription = "Student Details")
                        }
                    }
                    // Cart icon with badge
                    IconButton(onClick = { navController.navigate(Screen.CartScreen.route) }) {
                        BadgedBox(badge = { Badge { Text(mainViewModel.cartViewModel.cartItems.collectAsState().value.size.toString()) } }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = SchoolDetailsScreen.route, Modifier.padding(paddingValues)) {
            composable(route = SchoolDetailsScreen.route) {
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
                    mainViewModel.studentViewModel.clearStudentDetails()
                    mainViewModel.cartViewModel.clearCart()
                    ProductDetailScreen(navController = navController, categoryId = categoryId)
                }
            }
            composable(
                route = Screen.ProductScreen.route,
                arguments = listOf(navArgument(PRODUCT_ID) { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt(PRODUCT_ID) ?: 0
                ProductScreen(navController = navController, productId = productId, mainViewModel = mainViewModel)
            }

            // Cart Screen
            composable(route = Screen.CartScreen.route) {
                CartScreen(navController = navController, mainViewModel)
            }
            // Order Details Screen
            composable(route = Screen.OrderDetailsScreen.route) {
                OrderDetailsScreen(navController = navController, mainViewModel)
            }
            // Student details screen
            composable(route = Screen.StudentDetailsScreen.route) {
                StudentDetailsScreen(navController = navController, mainViewModel)
            }
        }
    }
}
