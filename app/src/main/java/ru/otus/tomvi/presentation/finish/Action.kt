package ru.otus.tomvi.presentation.finish

import ru.otus.tomvi.data.RaMCharacter

sealed interface Action {
    object LoadCharacters : Action
    object NotifyErrorShown : Action
    @JvmInline
    value class AddToFavorites(val id: Long) : Action
    @JvmInline
    value class RemoveFromFavorites(val id: Long) : Action

    // Internal
    data class CharactersHasLoaded(
        val characters: List<RaMCharacter>,
        val favorites: List<Long>,
    ) : Action

    object CharactersAreLoading : Action
    object FailureOfLoading : Action
}