package com.example.movies.network

import com.example.movies.model.Genre
import com.example.movies.model.Movie
import retrofit2.http.GET
import retrofit2.http.Path

interface MoviesApiService {

    @GET("genre/movie/list?api_key={api_key}&language=en")
    suspend fun getGenres(
        @Path("api_key") key: String
    ): List<Genre>

    @GET("discover/movie?api_key={api_key}&with_genres={genre_id}&page={page}&language=en")
    suspend fun getMovies(
        @Path("api_key") key: String,
        @Path("genre_id") genreId: String,
        @Path("page") page: Int
    ): List<Movie>

    @GET("movie/{movie_id}?api_key={api_key}&language=en")
    suspend fun getMoviesInfo(
        @Path("api_key") key: String,
        @Path("movie_id") movieId: String
    ): Movie
}