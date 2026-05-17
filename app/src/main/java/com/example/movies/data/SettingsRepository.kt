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
        val idStr = id.toString()
        val favorites = getFavoriteIds().toMutableSet()
        favorites.remove(idStr)
        prefs.edit { 
            putStringSet("favorites", favorites)
            remove("tag_$idStr")
            remove("rating_$idStr")
        }
    }

    fun getMovieTag(id: String): String? {
        return prefs.getString("tag_$id", null)
    }

    fun saveMovieTag(id: String, tag: String?) {
        prefs.edit { 
            if (tag == null) remove("tag_$id")
            else putString("tag_$id", tag)
        }
    }

    fun getMovieRating(id: String): Int {
        return prefs.getInt("rating_$id", -1)
    }

    fun saveMovieRating(id: String, rating: Int) {
        prefs.edit { 
            if (rating == -1) remove("rating_$id")
            else putInt("rating_$id", rating)
        }
    }
}
