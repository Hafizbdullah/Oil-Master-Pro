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
import com.example.presentation.onboarding.OnboardingScreen
import com.example.presentation.onboarding.OnboardingViewModel
import com.example.presentation.onboarding.OnboardingViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val onboardingViewModel: OnboardingViewModel = viewModel(
                    factory = OnboardingViewModelFactory(applicationContext)
                )
                val isOnboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = if (isOnboardingCompleted) Screen.CustomerList.route else Screen.Onboarding.route
                    ) {
                        composable(route = Screen.Onboarding.route) {
                            OnboardingScreen(
                                onFinish = {
                                    onboardingViewModel.completeOnboarding()
                                    navController.navigate(Screen.CustomerList.route) {
                                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }
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
