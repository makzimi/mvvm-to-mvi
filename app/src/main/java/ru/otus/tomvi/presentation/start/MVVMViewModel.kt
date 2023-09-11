package ru.otus.tomvi.presentation.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.otus.tomvi.data.CharactersRepository
import ru.otus.tomvi.data.FavoritesRepository

class MVVMViewModel(
    private val charactersRepository: CharactersRepository,
    private val favoritesRepository: FavoritesRepository,
    private val stateFactory: CharacterStateFactory,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        requestCharacters()
    }

    fun refresh() {
        requestCharacters()
    }

    private fun requestCharacters() {
        combine(
            charactersRepository.consumeCharacters(),
            favoritesRepository.consumeFavorites(),
        ) { characters, favorites -> stateFactory.create(characters, favorites) }
            .onStart {
                _uiState.update { currentState ->
                    currentState.copy(isLoading = true)
                }
            }
            .onEach { charactersState ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        characters = charactersState,
                        hasError = false,
                    )
                }
            }
            .catch {
                println("DBG: $it")
                _uiState.update { currentState ->
                    currentState.copy(hasError = true)
                }
            }
            .launchIn(viewModelScope)
    }

    fun errorHasShown() {
        _uiState.update { currentState ->
            currentState.copy(hasError = false)
        }
    }

    fun addToFavorites(id: Long) {
        viewModelScope.launch {
            favoritesRepository.addToFavorites(id)
        }
    }

    fun removeFromFavorites(id: Long) {
        viewModelScope.launch {
            favoritesRepository.removeFromFavorites(id)
        }
    }
}
