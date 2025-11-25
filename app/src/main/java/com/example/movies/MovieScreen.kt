package com.example.movies

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movies.ui.GenreScreen
import com.example.movies.ui.MovieDetailsScreen
import com.example.movies.ui.MoviesScreen
import com.example.movies.ui.StartScreen
import com.example.movies.viewModel.GenresViewModel
import com.example.movies.viewModel.MovieDetailsViewModel
import com.example.movies.viewModel.MoviesViewModel

// Available screens in app
enum class MovieScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Genres(title = R.string.select_genre),
    Movies(title = R.string.movie_list),
    MovieInfo(title = R.string.movie_info)
}

// Draw TopAppBar
@OptIn(ExperimentalMaterial3Api::class) //For using TopAppBar
@Composable
fun MovieAppBar(
    currentScreen: MovieScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentScreen != MovieScreen.Start) {
        TopAppBar(
            title = {
                Text(
                    stringResource(currentScreen.title),
                    color = Color.White
                )
            },
            modifier = modifier,
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(R.color.dark_yellow),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
    }
}

//Screens drawer
@Composable
fun MovieApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: MovieScreen.Start.name
    val baseRoute = route.substringBefore("/") // "Movies/28" → "Movies"
    val currentScreen = MovieScreen.valueOf(baseRoute)

    Scaffold(
        topBar = {
            MovieAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MovieScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Start screen
            composable(route = MovieScreen.Start.name) {
                StartScreen(
                    onStartButtonClicked = {
                        navController.navigate(MovieScreen.Genres.name)
                    }
                )
            }

            // Screen with list of genres
            composable(route = MovieScreen.Genres.name) {
                val genresViewModel: GenresViewModel = viewModel(factory = GenresViewModel.Factory)
                val uiState by genresViewModel.genresUiState.collectAsState()
                GenreScreen(
                    genresUiState = uiState,
                    retryAction = genresViewModel::getGenres,
                    showMovieList = { selectedGenre ->
                        navController.navigate("${MovieScreen.Movies.name}/${selectedGenre}")
                    }
                )
            }

            // Screen with list of movies on genre
            composable(
                route = "${MovieScreen.Movies.name}/{genreId}",
                arguments = listOf(navArgument("genreId") { type = NavType.StringType })
            ) {
                val genreId = backStackEntry?.arguments?.getString("genreId") ?: ""
                val moviesViewModel: MoviesViewModel = viewModel(factory = MoviesViewModel.Factory)
                val uiState by moviesViewModel.moviesUiState.collectAsState()

                LaunchedEffect(genreId) {
                    if (genreId.isNotEmpty()) {
                        moviesViewModel.loadMovies(genreId)
                    }
                }

                MoviesScreen(
                    moviesUiState = uiState,
                    retryAction = { moviesViewModel.loadMovies(genreId) },
                    onLoadMore = { moviesViewModel.loadMovies(genreId) },
                    onShowMovieDetails = { selectedMovie ->
                        navController.navigate("${MovieScreen.MovieInfo.name}/${selectedMovie}")
                    }
                )
            }

            // Screen movie details
            composable(
                route = "${MovieScreen.MovieInfo.name}/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) {
                val movieDetailsViewModel: MovieDetailsViewModel =
                    viewModel(factory = MovieDetailsViewModel.Factory)
                val uiState by movieDetailsViewModel.movieDetailsUiState.collectAsState()
                val movieId = backStackEntry?.arguments?.getString("movieId") ?: ""

                LaunchedEffect(movieId) {
                    if (movieId.isNotEmpty()) {
                        movieDetailsViewModel.getMovieDetails(movieId)
                    }
                }

                MovieDetailsScreen(
                    movieDetailsUiState = uiState,
                    retryAction = { movieDetailsViewModel.getMovieDetails(movieId) },
                )
            }

        }
    }
}