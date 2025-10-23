package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movies.data.mockMoviesResponse
import com.example.movies.model.Movie
import com.example.movies.viewModel.MoviesUiState
import com.example.movies.R

@Composable
fun MoviesScreen(
    moviesUiState: MoviesUiState,
    retryAction: () -> Unit,
    onLoadMore: () -> Unit,
    onShowMovieDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (moviesUiState) {
        is MoviesUiState.Loading -> LoadingScreen()
        is MoviesUiState.Error -> ErrorScreen(retryAction)
        is MoviesUiState.Success -> MoviesListScreen(
            moviesUiState.movies,
            onLoadMore = onLoadMore,
            onShowMovieDetails = onShowMovieDetails
        )
    }
}

@Composable
fun MoviesListScreen(
    movies: List<Movie>,
    onLoadMore: () -> Unit,
    onShowMovieDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.movies),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        LazyColumn(modifier = modifier) {
            itemsIndexed(movies) { index, movie ->
                MovieCard(movie = movie, onClick = onShowMovieDetails)

                if (index == movies.lastIndex) {
                    onLoadMore()
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
    Card (
        modifier = modifier
            .padding(start = 28.dp, end = 28.dp, bottom = 12.dp)
            .fillMaxWidth()
            .height(170.dp)
            .clickable { onClick(movie.id) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row (modifier = modifier.padding(8.dp)) {
            Box (
                modifier = Modifier
                    .fillMaxHeight()
                    .shadow( // TODO: Так и не получилось ничего адекватного
                        elevation = 100.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = movie.title,
                    error = painterResource(R.drawable.ic_broken_image),
                    placeholder = painterResource(R.drawable.loading_img),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = movie.title
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview,
                    maxLines = 3
                )
                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    RatingStars(movie.voteAverage)
                }
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
                tint = if (index < rating) colorResource(R.color.dark_yellow) else Color.Gray,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

@Preview
@Composable
fun MovieCardPreview() {
    MovieCard(mockMoviesResponse.movies[0], {})
}