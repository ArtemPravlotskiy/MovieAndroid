package com.example.movies.network

import com.example.movies.model.GenresResponse
import com.example.movies.model.MovieDetails
import com.example.movies.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApiService {

    @GET("proxy")
    suspend fun getGenres(
        @Query("endpoint") endpoint: String = "genre/movie/list",
        @Query("language") language: String = "ru"
    ): GenresResponse

    @GET("discover/movie")
    suspend fun getMovies(
        @Query("api_key") key: String,
        @Query("with_genres") genreId: String,
        @Query("page") page: Int,
        @Query("language") language: String = "en"
    ): MoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: String,
        @Query("api_key") key: String,
        @Query("language") language: String = "en"
    ): MovieDetails
}