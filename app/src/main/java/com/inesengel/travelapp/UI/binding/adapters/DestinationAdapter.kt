package com.inesengel.travelapp.UI.binding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inesengel.travelapp.R.string.price_tag
import com.inesengel.travelapp.databinding.ItemDestinationBinding
import project.model.TravelDestination

typealias DestinationClickListener = (TravelDestination) -> Unit

class DestinationAdapter(
    private val onItemClicked: DestinationClickListener
) : ListAdapter<TravelDestination, DestinationAdapter.DestinationViewHolder>(
    DestinationDiffCallback()
) {

    inner class DestinationViewHolder(
        val binding: ItemDestinationBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(destination: TravelDestination) {
            binding.apply {
                destinationNameTextView.text = destination.name
                destinationCountryTextView.text = destination.country
                val price = binding.root.context.getString(price_tag, destination.price.toString())
                destinationPriceTextView.text = price
                root.setOnClickListener {
                    onItemClicked(destination)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDestinationBinding.inflate(inflater, parent, false)

        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DestinationViewHolder,
        position: Int
    ) {
        val destination = getItem(position)
        holder.bind(destination)
    }
}

class DestinationDiffCallback : DiffUtil.ItemCallback<TravelDestination>() {

    override fun areItemsTheSame(
        oldItem: TravelDestination,
        newItem: TravelDestination
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TravelDestination,
        newItem: TravelDestination
    ): Boolean {
        return oldItem == newItem
    }
}
