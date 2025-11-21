package com.example.movies.network

import com.example.movies.model.GenresResponse
import com.example.movies.model.MovieDetails
import com.example.movies.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {

    @GET("proxy")
    suspend fun getGenres(
        @Query("endpoint") endpoint: String = "genre/movie/list",
        @Query("language") language: String = "ru"
    ): GenresResponse

    @GET("proxy")
    suspend fun getMovies(
        @Query("endpoint") endpoint: String = "discover/movie",
        @Query("with_genres") genreId: String,
        @Query("page") page: Int,
        @Query("language") language: String = "en"
    ): MoviesResponse

    @GET("proxy")
    suspend fun getMovieDetails(
        @Query("endpoint") endpoint: String,
        @Query("language") language: String = "en"
    ): MovieDetails
}