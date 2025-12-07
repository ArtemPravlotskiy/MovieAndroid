package com.example.movies.data

import android.content.Context
import com.example.movies.R
import com.example.movies.model.Genre
import com.example.movies.model.GenreDTO
import com.example.movies.network.MoviesApiService
import kotlinx.serialization.json.Json
import java.io.File

interface GenresRepository {
    suspend fun getGenres(): List<GenreDTO>
}

class NetworkGenresRepository(
    private val movieApiService: MoviesApiService,
    private val language: String,
    private val context: Context
) : GenresRepository {
    override suspend fun getGenres(): List<GenreDTO> {
        val localizedGenres = movieApiService.getGenres(language = language).genres
        val inputStream = context.resources.openRawResource(R.raw.genresid)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonId: List<Genre> = Json.decodeFromString<List<Genre>>(jsonString)

        return localizedGenres.map { genre ->
            val match = jsonId.find { it.id == genre.id }
            GenreDTO(
                id = genre.id,
                name = genre.name,
                imageName = match!!.name.lowercase().replace(" ", "")
            )
        }
    }
}