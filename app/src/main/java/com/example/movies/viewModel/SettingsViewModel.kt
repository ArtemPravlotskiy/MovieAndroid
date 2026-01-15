package com.example.movies.viewModel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.SettingsRepository
import com.example.movies.data.TextScale
import com.example.movies.data.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _selectedLanguage = MutableStateFlow(settingsRepository.getSavedLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedScale = MutableStateFlow(settingsRepository.getSavedTextScale())
    val selectedScale: StateFlow<TextScale> = _selectedScale

    private val _selectedTheme = MutableStateFlow(settingsRepository.getSavedTheme())
    val selectedTheme: StateFlow<Theme> = _selectedTheme

    private val _favoriteIds = MutableStateFlow(settingsRepository.getFavoriteIds())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    fun toggleFavorite(id: Int) {
        val currentFavorites = _favoriteIds.value.toMutableSet()
        val idStr = id.toString()
        if (currentFavorites.contains(idStr)) {
            settingsRepository.removeFavorite(id)
            currentFavorites.remove(idStr)
        } else {
            settingsRepository.addFavorite(id)
            currentFavorites.add(idStr)
        }
        _favoriteIds.value = currentFavorites
    }

    fun selectLanguage(code: String) {
        _selectedLanguage.value = code
        settingsRepository.saveLanguage(code)

        val locales = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun selectTextScale(scale: TextScale) {
        _selectedScale.value = scale
        settingsRepository.saveTextScale(scale)

        val context = settingsRepository.context
        val config = context.resources.configuration
        config.fontScale = scale.scaleFactor
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun selectTheme(theme: Theme) {
        _selectedTheme.value = theme
        settingsRepository.saveTheme(theme)

        when (theme) {
            Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
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