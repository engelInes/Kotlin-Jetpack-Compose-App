package com.inesengel.travelapp.UI.view.utils

import com.inesengel.travelapp.R

data class PresetImage(
    val id: Int,
    val resId: Int,
    val name: String
)

object PresetImageProvider {
    val presets = listOf(
        PresetImage(1, R.drawable.hong_kong, "Hong Kong"),
        PresetImage(2, R.drawable.malaga, "Malaga")
    )
}
