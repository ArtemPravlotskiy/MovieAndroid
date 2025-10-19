package com.example.movies.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movies.data.mockMoviesResponse
import com.example.movies.model.Genre
import com.example.movies.model.Movie
import com.example.movies.viewModel.MoviesUiState
import com.example.movies.R

@Composable
fun MoviesScreen(
    moviesUiState: MoviesUiState,
    retryAction: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (moviesUiState) {
        is MoviesUiState.Loading -> LoadingScreen()
        is MoviesUiState.Error -> ErrorScreen(retryAction)
        is MoviesUiState.Success -> MoviesListScreen(
            moviesUiState.movies,
            onLoadMore = onLoadMore
        )
    }
}

@Composable
fun MoviesListScreen(
    movies: List<Movie>,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(movies) { index, movie ->
            MovieCard(movie = movie, {})

            if (index == movies.lastIndex) {
                onLoadMore()
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
    Card (
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {onClick()},
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row (modifier = modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = movie.title
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingStars(movie.voteAverage)
            }
        }
    }
}

@Composable
fun RatingStars(rating: Double) {
    Row {
        repeat(10) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) Color.Yellow else Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun MovieCardPreview() {
    MovieCard(mockMoviesResponse.movies[0], {})
}