package ru.otus.tomvi.presentation.finish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.otus.tomvi.data.CharactersRepository
import ru.otus.tomvi.data.FavoritesRepository
import ru.otus.tomvi.presentation.CharacterStateFactory
import ru.otus.tomvi.presentation.UiState

class MVICharactersViewModel(
    private val charactersRepository: CharactersRepository,
    private val favoritesRepository: FavoritesRepository,
    private val stateFactory: CharacterStateFactory,
) : ViewModel(), ActionConsumer, StateHolder {

    private val exceptionHandler by lazy {
        CoroutineExceptionHandler { _, throwable ->
            println("Uncaught exception: $throwable")
        }
    }

    private val _actions: MutableSharedFlow<Action> by lazy {
        MutableSharedFlow(extraBufferCapacity = 1)
    }

    private val _uiState = MutableStateFlow(UiState())
    override val state: StateFlow<UiState>
        get() = _uiState.asStateFlow()

    init {
        val scope = viewModelScope + SupervisorJob() + exceptionHandler
        scope.launch {
            _actions
                .collect { action ->
                    val currentState = _uiState.value

                    _uiState.value = reduce(state = currentState, action = action)

                    scope.launch {
                        dispatchAction(state = currentState, action = action)
                    }
                }
        }
    }

    override fun consumeAction(action: Action) {
        _actions.tryEmit(action)
    }

    private suspend fun dispatchAction(state: UiState, action: Action) {
        when (action) {
            Action.LoadCharacters -> loadCharacters()
            Action.CharactersAreLoading -> Unit
            Action.NotifyErrorShown -> Unit
            is Action.AddToFavorites -> addToFavorites(action.id)
            is Action.RemoveFromFavorites -> removeToFavorites(action.id)
            is Action.CharactersHasLoaded -> Unit
            Action.FailureOfLoading -> Unit
        }
    }

    private suspend fun removeToFavorites(id: Long) {
        favoritesRepository.removeFromFavorites(id)
    }

    private suspend fun addToFavorites(id: Long) {
        favoritesRepository.addToFavorites(id)
    }

    private fun loadCharacters() {
        combine(
            charactersRepository.consumeCharacters(),
            favoritesRepository.consumeFavorites(),
        ) { characters, favorites ->
            consumeAction(Action.CharactersHasLoaded(characters, favorites))
        }
            .onStart {
                consumeAction(Action.CharactersAreLoading)
            }
            .catch {
                consumeAction(Action.FailureOfLoading)
            }
            .launchIn(viewModelScope)
    }

    private fun reduce(
        state: UiState,
        action: Action
    ): UiState {
        return when (action) {
            Action.LoadCharacters -> state
            Action.CharactersAreLoading -> state.copy(isLoading = true)
            Action.NotifyErrorShown -> state.copy(hasError = false)
            is Action.AddToFavorites -> state
            is Action.RemoveFromFavorites -> state
            is Action.CharactersHasLoaded -> state.copy(
                isLoading = false,
                hasError = false,
                characters = stateFactory.create(action.characters, action.favorites)
            )
            Action.FailureOfLoading -> state.copy(isLoading = false, hasError = true)
        }
    }
}
