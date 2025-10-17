package com.example.movies.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.network.HttpException
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.model.Movie
import kotlinx.coroutines.launch
import okio.IOException

sealed interface MoviesUiState {
    data class Success(val movies: List<Movie>) : MoviesUiState
    object Error : MoviesUiState
    object Loading : MoviesUiState
}

class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    var moviesUiState: MoviesUiState by mutableStateOf(MoviesUiState.Loading)

    init {
        getMovies(genreId = "28", page = 1)
    }

    fun getMovies(genreId: String, page: Int) {
        viewModelScope.launch {
            moviesUiState = MoviesUiState.Loading
            moviesUiState = try {
                MoviesUiState.Success (moviesRepository.getMovies(genreId, page))
            } catch (_: IOException) {
                MoviesUiState.Error
            } catch (_: HttpException) {
                MoviesUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                MoviesViewModel(moviesRepository = moviesRepository)
            }
        }
    }
}