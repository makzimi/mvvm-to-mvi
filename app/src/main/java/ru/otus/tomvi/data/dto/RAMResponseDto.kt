package ru.otus.tomvi.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RAMResponseDto(
    @SerialName("info") val info: ResponseDto,
    @SerialName("results") val results: List<CharacterDto>,
    @SerialName("error") val error: String?
)
