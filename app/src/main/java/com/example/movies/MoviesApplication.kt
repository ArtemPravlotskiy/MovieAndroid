package com.example.movies

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.movies.data.AppContainer
import com.example.movies.data.DefaultAppContainer
import com.example.movies.utils.updateLocale
import com.example.movies.data.SettingsRepository

class MoviesApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        val lang = container.settingsRepository.getSavedLanguage()
        if (lang.isNotEmpty()) {
            val locales = LocaleListCompat.forLanguageTags(lang)
            AppCompatDelegate.setApplicationLocales(locales)

        }
    }
}