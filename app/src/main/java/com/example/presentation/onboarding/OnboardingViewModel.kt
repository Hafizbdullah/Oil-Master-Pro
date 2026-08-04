package com.example.presentation.onboarding

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val ONBOARDING_COMPLETED_KEY = "onboarding_completed"

    private val _isOnboardingCompleted = MutableStateFlow(
        sharedPreferences.getBoolean(ONBOARDING_COMPLETED_KEY, false)
    )
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    fun completeOnboarding() {
        sharedPreferences.edit().putBoolean(ONBOARDING_COMPLETED_KEY, true).apply()
        _isOnboardingCompleted.value = true
    }
}

class OnboardingViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
