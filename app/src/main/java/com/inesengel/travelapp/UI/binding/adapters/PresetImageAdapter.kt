package com.inesengel.travelapp.UI.binding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inesengel.travelapp.databinding.ItemPresetImageBinding

class PresetImageAdapter(
    private val presets: List<Int>,
    private val onImageSelected: (Int) -> Unit
) : RecyclerView.Adapter<PresetImageAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPresetImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPresetImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val resId = presets[position]
        holder.binding.presetImageView.setImageResource(resId)

        holder.binding.root.setOnClickListener {
            onImageSelected(resId)
        }
    }

    override fun getItemCount() = presets.size
}
