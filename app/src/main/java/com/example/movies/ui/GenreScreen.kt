package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movies.R
import com.example.movies.data.GenresResponse
import com.example.movies.data.mockGenresResponse
import com.example.movies.model.Genre
import com.example.movies.ui.theme.MoviesTheme


@Composable
fun GenreScreen (
    genres: GenresResponse,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image( //TODO incorrect background image draw
            painter = painterResource(R.drawable.genres),
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.padding(13.dp)
        ) {
            items(genres.genres) { genre ->
                GenreBox(genre)
            }
        }
    }
}

@Composable
fun GenreBox(genre: Genre) {
    val resourceName = genre.name
        .lowercase()
        .replace(" ", "")

    val context = LocalContext.current
    val imageResId = remember(resourceName) {
        val resolvedId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resolvedId != 0) resolvedId else R.drawable.war
    }

    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        shape = RoundedCornerShape(0.dp), //TODO more shape for button
        modifier = Modifier.fillMaxSize().aspectRatio(1f).padding(15.dp) //TODO less padding top and bottom
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.5f)
            )
            Text( //TODO (not full text is displayed)
                text = resourceName,
                modifier = Modifier.fillMaxSize(0.2f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GenreScreenPreview() {
    MoviesTheme {
        GenreScreen(genres = mockGenresResponse)
    }
}

@Preview
@Composable
fun GenreBoxPreview() {
    GenreBox(Genre("28", "Action"))
}