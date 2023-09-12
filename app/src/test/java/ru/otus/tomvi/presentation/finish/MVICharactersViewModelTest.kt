package ru.otus.tomvi.presentation.finish

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.otus.tomvi.MainCoroutineRule
import ru.otus.tomvi.data.CharactersRepository
import ru.otus.tomvi.data.FavoritesRepository
import ru.otus.tomvi.data.RaMCharacter
import ru.otus.tomvi.presentation.CharacterStateFactory
import ru.otus.tomvi.presentation.UiState

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner.Silent::class)
class MVICharactersViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var sut: MVICharactersViewModel

    @Mock
    lateinit var charactersRepository: CharactersRepository

    @Mock
    lateinit var favoritesRepository: FavoritesRepository

    private val stateFactory = CharacterStateFactory()

    @Before
    fun setUp() {
        sut = MVICharactersViewModel(
            charactersRepository = charactersRepository,
            favoritesRepository = favoritesRepository,
            stateFactory = stateFactory,
        )

        whenever(charactersRepository.consumeCharacters()).thenReturn(flowOf())
        whenever(favoritesRepository.consumeFavorites()).thenReturn(flowOf())
    }

    @Test
    fun `WHEN LoadCharacters EXPECT isLoading state`() = runTest {
        sut.consumeAction(Action.LoadCharacters)
        advanceUntilIdle()
        val result = sut.state.value

        assertTrue(result.isLoading)
    }

    @Test
    fun `WHEN LoadCharacters EXPECT state with no error`() = runTest {
        whenever(charactersRepository.consumeCharacters()).thenReturn(
            flowOf(listOf(createCharacter(id = 1L), createCharacter(id = 2L)))
        )
        whenever(favoritesRepository.consumeFavorites()).thenReturn(flowOf(listOf()))

        sut.consumeAction(Action.LoadCharacters)
        val states = mutableListOf<UiState>()
        backgroundScope.launch { sut.state.collect { states.add(it) } }
        advanceUntilIdle()

        assertTrue(states.size == 3)
        assertTrue(states[0].isLoading.not())
        assertTrue(states[1].isLoading)
        assertTrue(states[2].isLoading.not())
        assertTrue(states[2].characters.size == 2)
    }

    @Test
    fun `WHEN LoadCharacters AND charactersRepository throw exception EXPECT error state`() =
        runTest {
            whenever(charactersRepository.consumeCharacters()).thenReturn(flow { throw IllegalArgumentException() })
            whenever(favoritesRepository.consumeFavorites()).thenReturn(flowOf(listOf()))

            sut.consumeAction(Action.LoadCharacters)
            advanceUntilIdle()
            val result = sut.state.value

            assertFalse(result.isLoading)
            assertTrue(result.hasError)
        }

    @Test
    fun `WHEN NotifyErrorShown EXPECT reset error state`() = runTest {
        whenever(charactersRepository.consumeCharacters()).thenReturn(flow { throw IllegalArgumentException() })
        whenever(favoritesRepository.consumeFavorites()).thenReturn(flowOf(listOf()))
        sut.consumeAction(Action.LoadCharacters)
        advanceUntilIdle()

        sut.consumeAction(Action.NotifyErrorShown)
        advanceUntilIdle()
        val result = sut.state.value

        assertFalse(result.hasError)
    }

    @Test
    fun `WHEN AddToFavorites EXPECT called add in favoritesRepository`() = runTest {
        whenever(charactersRepository.consumeCharacters()).thenReturn(
            flowOf(
                listOf(createCharacter(id = 1L), createCharacter(id = 2L))
            )
        )
        whenever(favoritesRepository.consumeFavorites()).thenReturn(flowOf(listOf()))
        sut.consumeAction(Action.LoadCharacters)
        advanceUntilIdle()

        sut.consumeAction(Action.AddToFavorites(1))
        advanceUntilIdle()

        verify(favoritesRepository).addToFavorites(1L)
    }

    private fun createCharacter(
        id: Long = 0L,
        name: String = "",
        image: String = "",
    ): RaMCharacter {
        return RaMCharacter(
            id = id,
            name = name,
            image = image,
        )
    }
}