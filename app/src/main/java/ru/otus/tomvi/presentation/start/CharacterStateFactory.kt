package ru.otus.tomvi.presentation.start

import ru.otus.tomvi.data.RaMCharacter

class CharacterStateFactory {
    fun create(characters: List<RaMCharacter>, favorites: List<Long>): List<CharacterState> {
        return characters.map { character ->
            CharacterState(
                id = character.id,
                name = character.name,
                image = character.image,
                isFavorite = favorites.contains(character.id),
            )
        }
    }
}
