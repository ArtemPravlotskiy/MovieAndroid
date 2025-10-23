package com.example.movies.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movies.R
import com.example.movies.model.MovieDetails

@Composable
fun MovieInfoScreen (
    movie: MovieDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black)
        ) {
            Column (
                modifier = Modifier.padding(8.dp)
            ) {
                Box (
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
                Row () {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )
                    /*Image(
                        painter = painterResource(R.drawable.adventure),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(0.4f)
                            .padding(start = 20.dp, end = 20.dp)
                            .size(90.dp)
                            .shadow(
                                elevation = 40.dp,
                                ambientColor = Color.White,
                                spotColor = Color.White,
                            )
                    )*/
                    // TODO вынести в отдельную функцию
                    Column (
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.padding(bottom = 10.dp)
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

                Row (
                    modifier = Modifier.padding(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.dark_yellow))
                    ) {
                        Text(
                            text = String.format("%.1f", movie.voteAverage),
                            color = Color.Black,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = movie.genres.joinToString(separator = ", ") { it.name },
                        color = colorResource(R.color.dark_yellow)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .weight(1f, fill = true)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black)
        ) {
            Text(
                text = movie.title,
                color = Color.White,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MovieInfoScreenPreview() {
    //MovieInfoScreen(mockMoviesResponse.movies[0])
}