package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movies.R
import com.example.movies.model.MovieDetails
import com.example.movies.viewModel.MovieDetailsUiState
import com.example.movies.viewModel.SettingsViewModel
import java.util.Locale

@Composable
fun MovieDetailsScreen(
    movieDetailsUiState: MovieDetailsUiState,
    retryAction: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    when (movieDetailsUiState) {
        is MovieDetailsUiState.Loading -> LoadingScreen()
        is MovieDetailsUiState.Error -> ErrorScreen(retryAction = retryAction)
        is MovieDetailsUiState.Success -> MovieInfoScreen(
            movie = movieDetailsUiState.movieDetails,
            settingsViewModel = settingsViewModel
        )
    }
}

@Composable
fun MovieInfoScreen(
    movie: MovieDetails,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    Box {
        Image(
            painter = painterResource(imageSelector(
                R.drawable.movies,
                R.drawable.movies_dark)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = 28.dp, start = 28.dp, bottom = 14.dp, top = 14.dp)
        ) {
            FirstBlock(movie = movie, settingsViewModel = settingsViewModel)

            Spacer(modifier = Modifier.height(5.dp))

            SecondBlock(movie = movie)
        }
    }
}

@Composable
fun FirstBlock(
    movie: MovieDetails,
    settingsViewModel: SettingsViewModel
) {
    val favoriteIds by settingsViewModel.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(movie.id.toString())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 10.dp, end = 10.dp)
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 30.sp
                )
            }

            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.height(270.dp)
                ) {

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(10.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://tmdb-proxy-production-4fbb.up.railway.app/image?path=${movie.posterPath}")
                                .build(),
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.loading_img),
                            error = painterResource(R.drawable.ic_broken_image),
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White)
                        ) {
                            Text(
                                text = movie.runtime.toString() + " min.",
                                color = Color.Gray,
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White)
                        ) {
                            Text(
                                text = movie.releaseDate,
                                color = Color.Gray,
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                        if (movie.tagline.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color.White)
                            ) {
                                Text(
                                    text = movie.tagline,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f", movie.voteAverage),
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp)
                ) {
                    Text(
                        text = movie.genres.joinToString(separator = ", ") { it.name },
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                }

                IconButton(onClick = { settingsViewModel.toggleFavorite(movie.id) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SecondBlock(
    movie: MovieDetails
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .fillMaxWidth()
    ) {
        Text(
            text = movie.overview.ifBlank { stringResource(R.string.no_description) },
            color = Color.White,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun MovieInfoScreenPreview() {
    //MovieInfoScreen(mockMoviesResponse.movies[0])
}
