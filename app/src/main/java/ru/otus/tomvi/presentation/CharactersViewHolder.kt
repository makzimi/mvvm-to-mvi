package ru.otus.tomvi.presentation

import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.otus.tomvi.R
import ru.otus.tomvi.databinding.ItemGalleryBinding

class CharactersViewHolder(
    private val binding: ItemGalleryBinding,
    private val onFavoriteClickListener: OnFavoriteClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(character: CharacterState) {
        binding.image.load(character.image)
        binding.name.text = character.name
        bindFavorite(character)
    }

    fun bindFavorite(character: CharacterState) {
        if (character.isFavorite) {
            binding.favorite.setImageResource(R.drawable.favorite_filled)
            binding.favorite.setColorFilter(
                ContextCompat.getColor(binding.root.context, R.color.pink),
                PorterDuff.Mode.SRC_IN,
            )
            binding.favorite.setOnClickListener {
                onFavoriteClickListener.onClick(id = character.id, favorite = false)
            }
        } else {
            binding.favorite.setImageResource(R.drawable.favorite)
            binding.favorite.setColorFilter(
                ContextCompat.getColor(binding.root.context, R.color.white),
                PorterDuff.Mode.SRC_IN,
            )
            binding.favorite.setOnClickListener {
                onFavoriteClickListener.onClick(id = character.id, favorite = true)
            }
        }
    }
}
