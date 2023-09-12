package ru.otus.tomvi.presentation.start

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.otus.tomvi.MainCoroutineRule
import ru.otus.tomvi.data.RaMCharacter

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner.Silent::class)
class CharactersViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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