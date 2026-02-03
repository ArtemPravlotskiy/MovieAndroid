package com.example.movies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import com.example.movies.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val moviesRepository: MoviesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _favoritesUiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val favoritesUiState: StateFlow<MoviesUiState> = _favoritesUiState

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favoriteIds = settingsRepository.getFavoriteIds()
                val favoriteMovies = favoriteIds.map { moviesRepository.getMovieDetails(it.toInt()) }
                _favoritesUiState.value = MoviesUiState.Success(favoriteMovies.map { movieDetails ->
                    Movie(
                        id = movieDetails.id,
                        title = movieDetails.title,
                        overview = movieDetails.overview,
                        posterPath = movieDetails.posterPath,
                        voteAverage = movieDetails.voteAverage
                    )
                })
            } catch (e: Exception) {
                _favoritesUiState.value = MoviesUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                val settingsRepository = application.container.settingsRepository
                FavoritesViewModel(moviesRepository = moviesRepository, settingsRepository = settingsRepository)
            }
        }
    }
}