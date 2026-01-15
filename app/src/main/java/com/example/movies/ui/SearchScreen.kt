package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movies.R
import com.example.movies.viewModel.SearchViewModel
import com.example.movies.viewModel.SettingsViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onShowMovieDetails: (Int) -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val searchResult by viewModel.searchResult.collectAsState()
    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // background
        Image(
            painter = painterResource(
                imageSelector(
                    R.drawable.movies,
                    R.drawable.movies_dark
                )
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.searchMovies(it)
                },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            LazyColumn {
                items(searchResult) { movie ->
                    MovieCard(movie = movie, onClick = onShowMovieDetails, settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}