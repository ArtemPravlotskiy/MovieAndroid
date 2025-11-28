package com.example.movies.data

data class AppLanguages(
    val languageCode: String,
    val language: String

    //val iconRes: id
)

val supportedLanguages = listOf(
    AppLanguages("ru", "Русский"),
    AppLanguages("en", "English"),
    AppLanguages("de", "Deutsch")
)