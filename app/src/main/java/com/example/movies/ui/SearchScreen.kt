package com.example.movies.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isVectorSearch by viewModel.isVectorSearch.collectAsState()
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
            SearchHeader(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.searchMovies(it)
                },
                isVectorSearch = isVectorSearch,
                onToggleMode = {
                    viewModel.toggleSearchMode()
                    if (query.isNotEmpty()) {
                        viewModel.searchMovies(query)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(searchResult) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = onShowMovieDetails,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    isVectorSearch: Boolean,
    onToggleMode: () -> Unit
) {
    val modeColor by animateColorAsState(
        targetValue = if (isVectorSearch) MaterialTheme.colorScheme.primary else Color.Gray,
        label = "modeColor"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = if (isVectorSearch) "Опишите фильм..." else "Название фильма...",
                        color = Color.LightGray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = modeColor,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = modeColor
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleMode() }
                    .background(modeColor.copy(alpha = 0.2f))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "AI Search",
                    tint = modeColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = if (isVectorSearch) "AI" else "Title",
                    color = modeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (isVectorSearch) {
            Text(
                text = "Включен поиск по смыслу (AI)",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
