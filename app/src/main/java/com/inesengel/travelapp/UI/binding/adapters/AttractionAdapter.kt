package com.inesengel.travelapp.UI.binding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inesengel.travelapp.databinding.ItemAttractionBinding
import com.inesengel.travelapp.core.model.DestinationAttraction
import androidx.core.net.toUri
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.NULL_INDEX

class AttractionAdapter(
    private val onItemClicked: (DestinationAttraction) -> Unit
) :
    ListAdapter<DestinationAttraction, AttractionAdapter.AttractionViewHolder>(
        AttractionDiffCallback()
    ) {
    inner class AttractionViewHolder(
        val binding: ItemAttractionBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item: DestinationAttraction) {
            binding.apply {
                highlightsAttractionsTextView.text = item.name
                highlightsDurationTextView.text = item.description
                when {
                    item.imagePath != null -> {
                        highlightsImageView.setImageURI(item.imagePath.toUri())
                    }

                    item.imageResId != NULL_INDEX -> {
                        highlightsImageView.setImageResource(item.imageResId)
                    }

                    else -> {
                        highlightsImageView.setImageDrawable(null)
                    }
                }

                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttractionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAttractionBinding.inflate(inflater, parent, false)
        return AttractionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AttractionViewHolder,
        position: Int
    ) {
        val attraction = getItem(position)
        holder.bind(attraction)
    }
}

class AttractionDiffCallback : DiffUtil.ItemCallback<DestinationAttraction>() {
    override fun areItemsTheSame(
        oldItem: DestinationAttraction,
        newItem: DestinationAttraction
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: DestinationAttraction,
        newItem: DestinationAttraction
    ): Boolean {
        return oldItem == newItem
    }
}

