package com.example.movies.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _selectedLanguage = MutableStateFlow(settingsRepository.getSavedLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    fun selectLanguage(code: String) {
        _selectedLanguage.value = code
        settingsRepository.saveLanguage(code)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val settingsRepository = application.container.settingsRepository
                SettingsViewModel(settingsRepository = settingsRepository)
            }
        }
    }
}