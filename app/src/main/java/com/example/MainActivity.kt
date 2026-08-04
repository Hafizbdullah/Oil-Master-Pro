package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.add_edit_customer.AddEditCustomerScreen
import com.example.presentation.customer_detail.CustomerDetailScreen
import com.example.presentation.customer_list.CustomerListScreen
import com.example.presentation.navigation.Screen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.CustomerList.route
                    ) {
                        composable(route = Screen.CustomerList.route) {
                            CustomerListScreen(
                                onNavigateToAddCustomer = {
                                    navController.navigate(Screen.AddEditCustomer.createRoute())
                                },
                                onNavigateToCustomerDetail = { id ->
                                    navController.navigate(Screen.CustomerDetail.createRoute(id))
                                }
                            )
                        }
                        composable(
                            route = Screen.AddEditCustomer.route,
                            arguments = listOf(
                                navArgument("customerId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) {
                            val customerId = it.arguments?.getLong("customerId") ?: -1L
                            AddEditCustomerScreen(
                                customerId = customerId,
                                onNavigateUp = {
                                    navController.navigateUp()
                                }
                            )
                        }
                        composable(
                            route = Screen.CustomerDetail.route,
                            arguments = listOf(
                                navArgument("customerId") {
                                    type = NavType.LongType
                                }
                            )
                        ) {
                            val customerId = it.arguments?.getLong("customerId") ?: -1L
                            CustomerDetailScreen(
                                customerId = customerId,
                                onNavigateUp = {
                                    navController.navigateUp()
                                },
                                onNavigateToEdit = { id ->
                                    navController.navigate(Screen.AddEditCustomer.createRoute(id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
