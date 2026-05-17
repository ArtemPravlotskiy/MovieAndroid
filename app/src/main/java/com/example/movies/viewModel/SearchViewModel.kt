package com.example.movies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.model.Movie
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _searchResult = MutableStateFlow<List<Movie>>(emptyList())
    val searchResult: StateFlow<List<Movie>> = _searchResult.asStateFlow()

    private val _isVectorSearch = MutableStateFlow(false)
    val isVectorSearch: StateFlow<Boolean> = _isVectorSearch.asStateFlow()

    private var searchJob: Job? = null

    fun toggleSearchMode() {
        _isVectorSearch.value = !_isVectorSearch.value
    }

    fun searchMovies(query: String) {
        searchJob?.cancel() // Отменяем предыдущий запрос, если он ещё не выполнился
        
        if (query.isBlank()) {
            _searchResult.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(1000) // Пауза в 1 секунду
            
            try {
                if (_isVectorSearch.value) {
                    val vectorResults = moviesRepository.vectorSearch(query)
                    val fullMovies = vectorResults.map { briefMovie ->
                        try {
                            val details = moviesRepository.getMovieDetails(briefMovie.id)
                            Movie(
                                id = details.id,
                                title = details.title,
                                overview = details.overview,
                                voteAverage = details.voteAverage,
                                posterPath = details.posterPath,
                                genreIds = details.genres.map { it.id }
                            )
                        } catch (e: Exception) {
                            briefMovie
                        }
                    }
                    _searchResult.value = fullMovies
                } else {
                    val movies = moviesRepository.searchMovies(query)
                    _searchResult.value = movies
                }
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                SearchViewModel(moviesRepository = moviesRepository)
            }
        }
    }
}
