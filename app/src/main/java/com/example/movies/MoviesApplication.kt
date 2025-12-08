package com.example.movies

import android.app.Application
import android.content.Context
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
        updateLocale(lang)
    }

    override fun attachBaseContext(base: Context) {
        val repo = SettingsRepository(base)
        val lang = repo.getSavedLanguage()
        super.attachBaseContext(base.updateLocale(lang))
    }
}