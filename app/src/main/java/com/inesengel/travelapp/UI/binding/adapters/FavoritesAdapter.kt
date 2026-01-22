package com.inesengel.travelapp.UI.binding.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.inesengel.travelapp.R
import com.inesengel.travelapp.databinding.ItemFavoriteDestinationBinding
import project.model.TravelDestination
import java.io.File

class FavoritesAdapter(
    private val onClick: (TravelDestination) -> Unit,
    private val onFavoriteIconClicked: (TravelDestination) -> Unit
) : ListAdapter<TravelDestination, FavoritesAdapter.FavoritesViewHolder>(
    FavoritesDiffCallback()
) {
    inner class FavoritesViewHolder(
        private val binding: ItemFavoriteDestinationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            destination: TravelDestination
        ) {
            binding.apply {
                destinationName.text = destination.name

                val imageFile = destination.imagePath?.let { File(it) }
                if (imageFile != null && imageFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    destinationImage.setImageBitmap(bitmap)
                } else {
                    if (destination.imageResId != 0) {
                        destinationImage.setImageResource(destination.imageResId)
                    } else {
                        destinationImage.setImageResource(R.drawable.no_picture)
                    }
                }
                root.setOnClickListener { onClick(destination) }
                favoriteIcon.setOnClickListener {
                    onFavoriteIconClicked(destination)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesViewHolder {
        val binding = ItemFavoriteDestinationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FavoritesViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}

class FavoritesDiffCallback : DiffUtil.ItemCallback<TravelDestination>() {
    override fun areItemsTheSame(
        oldItem: TravelDestination,
        newItem: TravelDestination
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: TravelDestination,
        newItem: TravelDestination
    ) = oldItem == newItem
}
