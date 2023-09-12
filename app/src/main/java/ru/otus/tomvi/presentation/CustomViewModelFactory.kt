package ru.otus.tomvi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.otus.tomvi.data.CharactersRepository
import ru.otus.tomvi.data.FavoritesRepository
import ru.otus.tomvi.presentation.finish.MVICharactersViewModel
import ru.otus.tomvi.presentation.start.CharactersViewModel

class CustomViewModelFactory(
    private val charactersRepository: CharactersRepository,
    private val favoritesRepository: FavoritesRepository,
    private val characterStateFactory: CharacterStateFactory,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(CharactersViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return CharactersViewModel(
                    charactersRepository = charactersRepository,
                    favoritesRepository = favoritesRepository,
                    stateFactory = characterStateFactory,
                ) as T
            }
            modelClass.isAssignableFrom(MVICharactersViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return MVICharactersViewModel(
                    charactersRepository = charactersRepository,
                    favoritesRepository = favoritesRepository,
                    stateFactory = characterStateFactory,
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
