package com.example.movies.data

import android.content.Context
import androidx.core.content.edit

class SettingsRepository(
    val context: Context
) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun saveLanguage(code: String) {
        prefs.edit { putString("language", code) }
    }

    fun getSavedLanguage(): String {
        return prefs.getString("language", "") ?: ""
    }

    fun saveTextScale(scale: TextScale) {
        prefs.edit { putString("textScale", scale.name) }
    }

    fun getSavedTextScale(): TextScale {
        val name = prefs.getString("textScale", TextScale.MEDIUM.name) ?: TextScale.MEDIUM.name
        return TextScale.valueOf(name)
    }

    fun saveTheme(theme: Theme) {
        prefs.edit { putString("theme", theme.name) }
    }

    fun getSavedTheme(): Theme {
        val name = prefs.getString("theme", Theme.LIGHT.name) ?: Theme.LIGHT.name
        return Theme.valueOf(name)
    }

    fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }

    fun addFavorite(id: Int) {
        val favorites = getFavoriteIds().toMutableSet()
        favorites.add(id.toString())
        prefs.edit { putStringSet("favorites", favorites) }
    }

    fun removeFavorite(id: Int) {
        val favorites = getFavoriteIds().toMutableSet()
        favorites.remove(id.toString())
        prefs.edit { putStringSet("favorites", favorites) }
    }
}