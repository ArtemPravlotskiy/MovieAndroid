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
import com.example.movies.data.GenresRepository
import com.example.movies.model.Genre
import kotlinx.coroutines.launch
import okio.IOException

sealed interface GenresUiState {
    data class Success(val genres: List<Genre>) : GenresUiState
    object Error : GenresUiState
    object Loading : GenresUiState
}

class GenresViewModel(
    private val genresRepository: GenresRepository
) : ViewModel() {
    var genresUiState: GenresUiState by mutableStateOf(GenresUiState.Loading)

    init {
        getGenres()
    }

    fun getGenres() {
        viewModelScope.launch {
            genresUiState = GenresUiState.Loading
            genresUiState = try {
                GenresUiState.Success (genresRepository.getGenres())
            } catch (_: IOException) {
                GenresUiState.Error
            } catch (_: HttpException) {
                GenresUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val genresRepository = application.container.genresRepository
                GenresViewModel(genresRepository = genresRepository)
            }
        }
    }
}