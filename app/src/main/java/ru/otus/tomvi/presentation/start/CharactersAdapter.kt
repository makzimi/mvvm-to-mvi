package ru.otus.tomvi.presentation.start

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.otus.tomvi.databinding.ItemGalleryBinding

class CharactersAdapter(
    private val onFavoriteClickListener: OnFavoriteClickListener,
) : ListAdapter<CharacterState, CharactersViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        return CharactersViewHolder(
            binding = ItemGalleryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onFavoriteClickListener = onFavoriteClickListener,
        )
    }

    override fun onBindViewHolder(
        holder: CharactersViewHolder,
        position: Int,
    ) {
        val entity = getItem(position)
        entity?.let(holder::bind)
    }

    override fun onBindViewHolder(
        holder: CharactersViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads[0] == true) {
                holder.bindFavorite(getItem(position))
            }
        }
    }
}

interface OnFavoriteClickListener {
    fun onClick(id: Long, favorite: Boolean)
}

private class DiffCallback : DiffUtil.ItemCallback<CharacterState>() {

    override fun areItemsTheSame(oldItem: CharacterState, newItem: CharacterState): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CharacterState, newItem: CharacterState): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: CharacterState, newItem: CharacterState): Any? {
        if (oldItem.isFavorite != newItem.isFavorite) {
            return true
        }

        return null
    }
}