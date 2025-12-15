package com.example.movies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.model.MovieDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface MovieDetailsUiState {
    data class Success(val movieDetails: MovieDetails) : MovieDetailsUiState
    object Error : MovieDetailsUiState
    object Loading : MovieDetailsUiState
}

class MovieDetailsViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val _movieDetailsUiState =
        MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val movieDetailsUiState: StateFlow<MovieDetailsUiState> = _movieDetailsUiState

    fun getMovieDetails(movieId: String) {
        viewModelScope.launch {
            _movieDetailsUiState.value = MovieDetailsUiState.Loading
            _movieDetailsUiState.value = try {
                MovieDetailsUiState.Success(
                    moviesRepository.getMovieDetails(movieId = movieId.toInt())
                )
            } catch (e: IOException) {
                MovieDetailsUiState.Error
            } catch (e: HttpException) {
                MovieDetailsUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                MovieDetailsViewModel(moviesRepository = moviesRepository)
            }
        }
    }
}