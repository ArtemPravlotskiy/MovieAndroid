package com.example.movies.data

import android.content.Context
import com.example.movies.network.MoviesApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.Locale

interface AppContainer {
    val moviesRepository: MoviesRepository
    val genresRepository: GenresRepository
    val movieDetailsRepository: MovieDetailsRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
//    private val baseUrl = "https://api.themoviedb.org/3/"
    private val baseUrl = "https://tmdb-proxy-production-4fbb.up.railway.app/"

    val json = Json { ignoreUnknownKeys = true }
    private val language = Locale.getDefault().language

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    private val retrofit: Retrofit = Retrofit.Builder().client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl).build()

    private val retrofitService: MoviesApiService by lazy {
        retrofit.create(MoviesApiService::class.java)
    }

    override val genresRepository: GenresRepository by lazy {
        NetworkGenresRepository(retrofitService, language, context = context)
    }

    override val moviesRepository: MoviesRepository by lazy {
        NetworkMoviesRepository(retrofitService, language)
    }

    override val movieDetailsRepository: MovieDetailsRepository by lazy {
        NetworkMovieDetailsRepository(retrofitService, language)
    }
}