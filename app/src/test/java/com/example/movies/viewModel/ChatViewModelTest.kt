package com.example.movies.viewModel

import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import com.example.movies.model.ChatResponse
import com.example.movies.model.Movie
import com.example.movies.model.Recommendation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val moviesRepository: MoviesRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private lateinit var viewModel: ChatViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { settingsRepository.getFavoriteIds() } returns emptySet()
        viewModel = ChatViewModel(moviesRepository, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has welcome message`() {
        val messages = viewModel.messages.value
        assertEquals(1, messages.size)
        assertEquals("Привет! Я твой кино-ассистент. Какой фильм хочешь посмотреть сегодня?", messages[0].text)
        assertEquals(false, messages[0].isUser)
    }

    @Test
    fun `sendMessage with blank text does nothing`() {
        viewModel.sendMessage("   ")
        assertEquals(1, viewModel.messages.value.size)
    }

    @Test
    fun `sendMessage success path handles AI response and movie search`() = runTest {
        // Given
        val text = "Хочу фантастику"
        coEvery { settingsRepository.getFavoriteIds() } returns setOf("1", "2")

        val mockAiResponse = ChatResponse(
            explanation = "Рекомендую Интерстеллар",
            recommendations = listOf(Recommendation(title = "Interstellar", reason = "Because")),
            follow_up = "Что думаешь?"
        )
        coEvery { moviesRepository.chatWithAi(any()) } returns mockAiResponse

        val mockMovie = mockk<Movie> { coEvery { id } returns 42 }
        coEvery { moviesRepository.searchMovies("Interstellar") } returns listOf(mockMovie)

        // When
        viewModel.sendMessage(text)

        // Then
        val messages = viewModel.messages.value
        assertEquals(3, messages.size)
        assertEquals(text, messages[1].text)
        assertTrue(messages[1].isUser)

        val aiMsg = messages[2]
        assertEquals(false, aiMsg.isUser)
        assertTrue(aiMsg.text.contains("Рекомендую Интерстеллар"))
        assertTrue(aiMsg.text.contains("Что думаешь?"))
        assertEquals(1, aiMsg.recommendations.size)
        assertEquals(mockMovie, aiMsg.recommendations[0].movie)
    }
}

@RunWith(Parameterized::class)
class ChatViewModelErrorsTest(private val exception: Exception) {

    private val moviesRepository: MoviesRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private lateinit var viewModel: ChatViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { settingsRepository.getFavoriteIds() } returns emptySet()
        viewModel = ChatViewModel(moviesRepository, settingsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Сбой ИИ: {0}")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(IOException("No network")),
            arrayOf(RuntimeException("Gemini API quota exceeded")),
            arrayOf(NullPointerException("Unexpected empty body"))
        )
    }

    @Test
    fun `sendMessage handles AI errors gracefully`() = runTest {
        coEvery { moviesRepository.chatWithAi(any()) } throws exception

        viewModel.sendMessage("Привет")

        val messages = viewModel.messages.value
        assertEquals(3, messages.size)
        assertEquals("Извини, произошла ошибка при общении с ИИ.", messages[2].text)
        assertEquals(false, viewModel.isLoading.value)
    }
}