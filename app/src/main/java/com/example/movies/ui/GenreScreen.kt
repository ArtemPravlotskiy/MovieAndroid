package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.movies.R
import com.example.movies.ui.theme.MoviesTheme

@Composable
fun GenreScreen (
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.genres),
            contentDescription = null
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun GenreScreenPreview() {
    MoviesTheme {
        GenreScreen()
    }
}