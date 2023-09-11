package ru.otus.tomvi.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CharactersRepository(
    private val api: RAMRetrofitService
) {
    fun consumeCharacters(): Flow<List<RaMCharacter>> = flow {
        val characters = api.getCharacters().results
            .map { dto ->
                RaMCharacter(
                    id = dto.id,
                    name = dto.name,
                    image = dto.image,
                )
            }

        emit(characters)
    }
}
