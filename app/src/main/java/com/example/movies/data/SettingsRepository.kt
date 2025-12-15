package com.example.movies.data

import android.content.Context
import androidx.core.content.edit

class SettingsRepository(
    val context: Context
) {
    fun saveLanguage(code: String) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit { putString("language", code) }
    }

    fun getSavedLanguage(): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("language", "") ?: ""
    }

    fun saveTextScale(scale: TextScale) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit { putString("textScale", scale.name) }
    }

    fun getSavedTextScale(): TextScale {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val name = prefs.getString("textScale", TextScale.MEDIUM.name) ?: TextScale.MEDIUM.name
        return TextScale.valueOf(name)
    }

    fun saveTheme(theme: Theme) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit { putString("theme", theme.name) }
    }

    fun getSavedTheme(): Theme {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val name = prefs.getString("theme", Theme.LIGHT.name) ?: Theme.LIGHT.name
        return Theme.valueOf(name)
    }
}