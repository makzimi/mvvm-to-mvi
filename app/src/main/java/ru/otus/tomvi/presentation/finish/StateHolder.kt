package ru.otus.tomvi.presentation.finish

import kotlinx.coroutines.flow.StateFlow
import ru.otus.tomvi.presentation.UiState

interface StateHolder {
    val state: StateFlow<UiState>
}