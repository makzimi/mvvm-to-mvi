package ru.otus.tomvi.presentation.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.otus.tomvi.data.CharactersRepository
import ru.otus.tomvi.data.FavoritesRepository

class CustomViewModelFactory(
    private val charactersRepository: CharactersRepository,
    private val favoritesRepository: FavoritesRepository,
    private val characterStateFactory: CharacterStateFactory,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(MVVMViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return MVVMViewModel(
                    charactersRepository = charactersRepository,
                    favoritesRepository = favoritesRepository,
                    stateFactory = characterStateFactory,
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
