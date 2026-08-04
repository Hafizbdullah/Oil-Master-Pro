package com.example.presentation.navigation

sealed class Screen(val route: String) {
    object CustomerList : Screen("customer_list")
    object AddEditCustomer : Screen("add_edit_customer?customerId={customerId}") {
        fun createRoute(customerId: Long? = null) = "add_edit_customer?customerId=${customerId ?: -1L}"
    }
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: Long) = "customer_detail/$customerId"
    }
}
