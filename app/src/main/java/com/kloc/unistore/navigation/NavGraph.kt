package com.kloc.unistore.navigation


import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.kloc.unistore.R
import com.kloc.unistore.firestoredb.viewmodel.EmployeeViewModel
import com.kloc.unistore.model.productCategoryViewModel.SchoolCategoryViewModel
import com.kloc.unistore.model.productViewModel.ProductViewModel
import com.kloc.unistore.model.schoolViewModel.SchoolViewModel
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen.SchoolDetailsScreen
import com.kloc.unistore.screens.CartScreen
import com.kloc.unistore.screens.OrderDetailsScreen
import com.kloc.unistore.screens.ProductDetailScreen
import com.kloc.unistore.screens.ProductScreen
import com.kloc.unistore.screens.SchoolDetailsScreen
import com.kloc.unistore.screens.SchoolCategoryScreen
import com.kloc.unistore.screens.SignInScreen
import com.kloc.unistore.screens.StudentDetailsScreen
import com.kloc.unistore.util.Constants.CATEGORY_ID
import com.kloc.unistore.util.Constants.PRODUCT_ID
import com.kloc.unistore.util.Constants.SCHOOL_ID
import com.kloc.unistore.navigation.Screen.*
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@ExperimentalComposeUiApi
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    schoolCategoryViewModel: SchoolCategoryViewModel= hiltViewModel(),
    schoolViewModel: SchoolViewModel = hiltViewModel(),
    employeesViewModel: EmployeeViewModel = hiltViewModel()
) {
    // Observe the current route in the back stack
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val productViewModel: ProductViewModel = hiltViewModel()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.Center) {
                        when (currentRoute) {
                            SchoolDetailsScreen.route -> { Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Unistore Logo", modifier = Modifier.size(160.dp).padding(start = 5.dp, end = 8.dp)) }
                            SchoolCategoryScreen.route -> { Text(text = "Select Grade", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center , fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) }
                            ProductDetailsScreen.route -> { Text(text = "Select Gender", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center , fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) }
                            ProductScreen.route -> { Text(text = "Add Products", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center , fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) }
                            CartScreen.route -> { Text(text = "Cart", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center , fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) }
                            StudentDetailsScreen.route -> { Text(text = "Student Details", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center , fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) }
                            OrderDetailsScreen.route -> { Text(text = "Order Summary", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center , fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) }
                            else -> { Text("") }
                        }
                    }
                },
                navigationIcon = {
                    if (currentRoute != SignInScreen.route) {
                        if (currentRoute == SchoolCategoryScreen.route) {
                            BackHandler(enabled = true) { navController.navigate(SchoolDetailsScreen.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } } }
                            IconButton(onClick = { navController.navigate(SchoolDetailsScreen.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } } }) { Icon(Icons.Default.ArrowBack, contentDescription = "Log Out") }
                        } else if (currentRoute == ProductDetailsScreen.route) {
                            if (mainViewModel.logOut) {
                                BackHandler(enabled = true) { navController.navigate(SchoolDetailsScreen.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } } }
                                IconButton(onClick = { navController.navigate(SchoolDetailsScreen.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } } }) { Icon(Icons.Default.ArrowBack, contentDescription = "Log Out") }
                            } else { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
                        } else if (currentRoute != SchoolDetailsScreen.route && currentRoute != ProductDetailsScreen.route) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
                    }
                },
                actions = {
                    when (currentRoute) {
                        OrderDetailsScreen.route -> {
                            IconButton(onClick = { navController.navigate(StudentDetailsScreen.route) }) {
                                Icon(Icons.Default.Person, contentDescription = "Student Details")
                            }
                        }
                        ProductScreen.route -> {
                            IconButton(onClick = { navController.navigate(CartScreen.route) }) {
                                BadgedBox(badge = { Badge { Text(mainViewModel.cartViewModel.cartItems.collectAsState().value.size.toString()) } }) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                                }
                            }
                        }
                        else -> {
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                    }
                }
            )
        }
    )
    { paddingValues ->
        NavHost(navController = navController, startDestination = SignInScreen.route, Modifier.padding(paddingValues)) {

            composable(route =SignInScreen.route  ) {
                SignInScreen(navController =navController,employeeViewModel=employeesViewModel , mainViewModel= mainViewModel)
            }
            composable(route = SchoolDetailsScreen.route) {
                schoolViewModel.resetSchoolDetails() // AJ
                SchoolDetailsScreen(navController = navController,employeeViewModel=employeesViewModel)
            }
            composable(
                route = SchoolCategoryScreen.route,
                arguments = listOf(navArgument(SCHOOL_ID) { type = NavType.IntType })
            ) { backStackEntry ->
                val schoolId = backStackEntry.arguments?.getInt(SCHOOL_ID)
                if (schoolId != null) {
                   schoolCategoryViewModel.resetCategories() // AJ
                    SchoolCategoryScreen(navController = navController, schoolId = schoolId,mainViewModel=mainViewModel)
                }
            }
            composable(
                route = ProductDetailsScreen.route,
                arguments = listOf(navArgument(CATEGORY_ID) { type = NavType.IntType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getInt(CATEGORY_ID)
                if (categoryId != null) {
                    mainViewModel.studentViewModel.clearStudentDetails()
                    mainViewModel.cartViewModel.clearCart()
                    ProductDetailScreen(navController = navController, categoryId = categoryId,viewModel=productViewModel)
                }
            }
            composable(
                route = ProductScreen.route,
                arguments = listOf(navArgument(PRODUCT_ID) { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt(PRODUCT_ID) ?: 0
                productViewModel.resetProductData()     // AJ
                ProductScreen(navController = navController, productId = productId,viewModel=productViewModel, mainViewModel = mainViewModel)
            }
            // Cart Screen
            composable(route = CartScreen.route) {
                CartScreen(navController = navController, mainViewModel)
            }
            // Order Details Screen
            composable(route = OrderDetailsScreen.route) {
                OrderDetailsScreen(navController = navController, mainViewModel,productViewModel=productViewModel,employeeViewModel=employeesViewModel)
            }
            // Student details screen
            composable(route = StudentDetailsScreen.route) {
                StudentDetailsScreen(navController = navController, mainViewModel,employeesViewModel)
            }
        }
    }
}










//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//@ExperimentalComposeUiApi
//fun NavGraph(
//    navController: NavHostController,
//    mainViewModel: MainViewModel
//) {
//    // Observe the current route in the back stack
//    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//    val productViewModel: ProductViewModel = hiltViewModel()
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Unipro") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    // Show the person icon only on the OrderDetailsScreen
//                    if (currentRoute == Screen.OrderDetailsScreen.route) {
//                        IconButton(onClick = { navController.navigate(Screen.StudentDetailsScreen.route) }) {
//                            Icon(Icons.Default.Person, contentDescription = "Student Details")
//                        }
//                    }
//                    // Cart icon with badge
//                    IconButton(onClick = { navController.navigate(Screen.CartScreen.route) }) {
//                        BadgedBox(badge = { Badge { Text(mainViewModel.cartViewModel.cartItems.collectAsState().value.size.toString()) } }) {
//                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
//                        }
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        NavHost(navController = navController, startDestination = SchoolDetailsScreen.route, Modifier.padding(paddingValues)) {
//            composable(route = SchoolDetailsScreen.route) {
//                SchoolDetailsScreen(navController = navController)
//            }
//            composable(
//                route = Screen.SchoolCategoryScreen.route,
//                arguments = listOf(navArgument(SCHOOL_ID) { type = NavType.IntType })
//            ) { backStackEntry ->
//                val schoolId = backStackEntry.arguments?.getInt(SCHOOL_ID)
//                if (schoolId != null) {
//                    SchoolCategoryScreen(navController = navController, schoolId = schoolId)
//                }
//            }
//
//            composable(
//                route = Screen.ProductDetailsScreen.route,
//                arguments = listOf(navArgument(CATEGORY_ID) { type = NavType.IntType })
//            ) { backStackEntry ->
//                val categoryId = backStackEntry.arguments?.getInt(CATEGORY_ID)
//                if (categoryId != null) {
//                    mainViewModel.studentViewModel.clearStudentDetails()
//                    mainViewModel.cartViewModel.clearCart()
//                    ProductDetailScreen(navController = navController, categoryId = categoryId,viewModel=productViewModel)
//                }
//            }
//            composable(
//                route = Screen.ProductScreen.route,
//                arguments = listOf(navArgument(PRODUCT_ID) { type = NavType.IntType })
//            ) { backStackEntry ->
//                val productId = backStackEntry.arguments?.getInt(PRODUCT_ID) ?: 0
//                ProductScreen(navController = navController, productId = productId,viewModel=productViewModel, mainViewModel = mainViewModel)
//            }
//
//            // Cart Screen
//            composable(route = Screen.CartScreen.route) {
//                CartScreen(navController = navController, mainViewModel)
//            }
//            // Order Details Screen
//            composable(route = Screen.OrderDetailsScreen.route) {
//                OrderDetailsScreen(navController = navController, mainViewModel,productViewModel=productViewModel)
//            }
//            // Student details screen
//            composable(route = Screen.StudentDetailsScreen.route) {
//                StudentDetailsScreen(navController = navController, mainViewModel)
//            }
//        }
//    }
//}
