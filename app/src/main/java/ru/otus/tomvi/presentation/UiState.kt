package ru.otus.tomvi.presentation

data class UiState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val characters: List<CharacterState> = emptyList()
)

data class CharacterState(
    val id: Long = 0L,
    val name: String = "",
    val image: String = "",
    val isFavorite: Boolean = false,
)
