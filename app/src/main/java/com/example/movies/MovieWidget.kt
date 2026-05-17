package com.example.movies

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.movies.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class MovieWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val movies = getWatchingMovies(context)

        provideContent {
            GlanceTheme {
                WidgetContent(movies)
            }
        }
    }

    @Composable
    private fun WidgetContent(movies: List<MovieWidgetData>) {
        val context = LocalContext.current
        
        if (movies.isEmpty()) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.no_movies_watching),
                    style = TextStyle(color = GlanceTheme.colors.onSurface)
                )
            }
        } else {
            LazyColumn(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
            ) {
                items(movies) { movie ->
                    MovieItem(movie)
                }
            }
        }
    }

    @Composable
    private fun MovieItem(movie: MovieWidgetData) {
        val context = LocalContext.current
        
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("moviesflow://movie/${movie.id}"),
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(actionStartActivity(intent)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок (Название сверху)
            Text(
                text = movie.title,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            
            Spacer(GlanceModifier.height(4.dp))

            // Постер под названием
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(180.dp) // Высота примерно для 2x3 сетки
                    .background(GlanceTheme.colors.secondaryContainer)
            ) {
                if (movie.posterBitmap != null) {
                    Image(
                        provider = ImageProvider(movie.posterBitmap),
                        contentDescription = movie.title,
                        modifier = GlanceModifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No Poster", 
                            style = TextStyle(color = GlanceTheme.colors.onSecondaryContainer)
                        )
                    }
                }
                
                // Тег "Смотрю" поверх постера
                Box(
                    modifier = GlanceModifier
                        .padding(4.dp)
                        .background(GlanceTheme.colors.primary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Смотрю",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(GlanceModifier.height(12.dp)) // Отступ между фильмами для имитации раздельных страниц
        }
    }

    private suspend fun getWatchingMovies(context: Context): List<MovieWidgetData> = withContext(Dispatchers.IO) {
        val app = context.applicationContext as MoviesApplication
        val settings = app.container.settingsRepository
        val repository = app.container.moviesRepository
        
        val favoriteIds = settings.getFavoriteIds()
        val watchingIds = favoriteIds.filter { id ->
            settings.getMovieTag(id) == "Смотрю"
        }

        watchingIds.mapNotNull { id ->
            try {
                val details = repository.getMovieDetails(id.toInt())
                val bitmap = details.posterPath?.let { path ->
                    downloadBitmap("https://tmdb-proxy-ziqk.onrender.com/image?path=$path")
                }
                MovieWidgetData(
                    id = details.id,
                    title = details.title,
                    posterBitmap = bitmap
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun downloadBitmap(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection()
            connection.connect()
            val input = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }
}

data class MovieWidgetData(
    val id: Int,
    val title: String,
    val posterBitmap: Bitmap?
)

class MovieWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MovieWidget()
}
