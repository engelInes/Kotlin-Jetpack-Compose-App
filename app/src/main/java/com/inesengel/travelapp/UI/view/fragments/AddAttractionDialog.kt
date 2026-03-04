package com.inesengel.travelapp.UI.view.fragments

import android.content.Context
import android.view.LayoutInflater
import com.inesengel.travelapp.R
import com.inesengel.travelapp.databinding.AddAttractionDialogBinding
import android.app.AlertDialog
import android.net.Uri
import androidx.recyclerview.widget.LinearLayoutManager
import com.inesengel.travelapp.UI.binding.adapters.PresetImageAdapter
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.NULL_INDEX
import com.inesengel.travelapp.UI.view.utils.PresetImageProvider
import com.inesengel.travelapp.core.model.DestinationAttraction

class AddAttractionDialog(
    private val context: Context,
    private val onPickImageClicked: () -> Unit,
    private val onConfirm: (DestinationAttraction) -> Unit
) {
    private var binding: AddAttractionDialogBinding? = null
    private var currentDialog: AlertDialog? = null

    private var selectedUri: String? = null

    private var selectedResId: Int = PresetImageProvider.presets.firstOrNull()?.resId ?: NULL_INDEX

    fun displayDialog() {
        binding = AddAttractionDialogBinding.inflate(LayoutInflater.from(context))

        binding?.apply {
            pickGalleryButton.setOnClickListener {
                onPickImageClicked()
            }

            presetsRecyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            val drawableList = PresetImageProvider.presets.map { it.resId }
            presetsRecyclerView.adapter = PresetImageAdapter(drawableList) { resId ->
                selectedResId = resId
                selectedUri = null

                attractionImagePreview.setImageResource(resId)
            }

            attractionImagePreview.setImageResource(selectedResId)
        }

        currentDialog = AlertDialog.Builder(context)
            .setTitle(R.string.add_attraction_desc)
            .setView(binding?.root)
            .setPositiveButton(R.string.add_button_name) { _, _ ->
                binding?.let {
                    onConfirm(
                        DestinationAttraction(
                            id = NULL_INDEX,
                            destinationId = NULL_INDEX,
                            name = it.attractionTitleInput.text.toString(),
                            description = it.attractionDescriptionInput.text.toString(),
                            duration = NULL_INDEX,
                            imageResId = selectedResId,
                            imagePath = selectedUri
                        )
                    )
                }
            }
            .setNegativeButton(
                R.string.cancel_button_name,
                null
            )
            .show()
    }

    fun updateImage(uri: Uri) {
        selectedUri = uri.toString()
        selectedResId = NULL_INDEX

        binding?.attractionImagePreview?.setImageURI(uri)
    }
}
