package com.example.movies.data

import com.example.movies.network.MoviesApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val moviesRepository: MoviesRepository
    val genresRepository: GenresRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://api.themoviedb.org/3/"

    val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: MoviesApiService by lazy {
        retrofit.create(MoviesApiService::class.java)
    }

    override val genresRepository: GenresRepository by lazy {
        NetworkGenresRepository(retrofitService)
    }

    override val moviesRepository: MoviesRepository by lazy {
        NetworkMoviesRepository(retrofitService)
    }
}