package com.example.movies.data

import com.example.movies.model.Genre
import com.example.movies.model.Movie

data class GenresResponse(val genres: List<Genre>)

data class MoviesResponse(
    val page: Int,
    val movies: List<Movie>
)

val mockMoviesResponse = MoviesResponse (
    page = 1,
    movies = listOf(
        Movie(
            id = 1311031,
            title = "Demon Slayer: Kimetsu no Yaiba Infinity Castle",
            originalTitle = "劇場版「鬼滅の刃」無限城編 第一章 猗窩座再来",
            originalLanguage = "ja",
            overview = "The Demon Slayer Corps are drawn into the Infinity Castle...",
            releaseDate = "2025-07-18",
            popularity = 284.9423,
            voteAverage = 7.801,
            voteCount = 463,
            adult = false,
            video = false,
            genreIds = listOf(16, 28, 14, 53),
            posterPath = "/sUsVimPdA1l162FvdBIlmKBlWHx.jpg",
            backdropPath = "/1RgPyOhN4DRs225BGTlHJqCudII.jpg"
        ),
        Movie(
            id = 1086910,
            title = "The Lost Princess",
            originalTitle = "The Lost Princess",
            originalLanguage = "en",
            overview = "After an Ayahuasca vision transports him to a haunted castle...",
            releaseDate = "2025-10-16",
            popularity = 280.8371,
            voteAverage = 6.44,
            voteCount = 25,
            adult = false,
            video = false,
            genreIds = listOf(28, 12),
            posterPath = "/31S2ISsDtbnxb0kuXZl1SxSMD0K.jpg",
            backdropPath = "/ax2qCKU6tUhdkStiCnrDdXKA5xC.jpg"
        ),
        Movie(
            id = 338969,
            title = "The Toxic Avenger Unrated",
            originalTitle = "The Toxic Avenger Unrated",
            originalLanguage = "en",
            overview = "When a downtrodden janitor is exposed to a toxic accident...",
            releaseDate = "2025-08-28",
            popularity = 262.4798,
            voteAverage = 6.283,
            voteCount = 127,
            adult = false,
            video = false,
            genreIds = listOf(28, 35, 878),
            posterPath = "/sIonGSpGNtH72OzbJllPOEMNjVU.jpg",
            backdropPath = "/wyg8OaiDFru4NWuEnhCIsF3W1Ek.jpg"
        )
    )
)

val mockGenresResponse = GenresResponse(
    genres = listOf(
        Genre("28", "Action"),
        Genre("12", "Adventure"),
        Genre("16", "Animation"),
        Genre("35", "Comedy"),
        Genre("80", "Crime"),
        Genre("99", "Documentary"),
        Genre("18", "Drama"),
        Genre("10751", "Family"),
        Genre("14", "Fantasy"),
        Genre("36", "History"),
        Genre("27", "Horror"),
        Genre("10402", "Music"),
        Genre("9648", "Mystery"),
        Genre("10749", "Romance"),
        Genre("878", "Science Fiction"),
        Genre("10770", "TV Movie"),
        Genre("53", "Thriller"),
        Genre("10752", "War"),
        Genre("37", "Western")
    )
)
