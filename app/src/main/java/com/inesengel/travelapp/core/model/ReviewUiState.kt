package com.inesengel.travelapp.UI.model

data class ReviewStats(
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val ratingRows: List<RatingRowState> = emptyList()
)

data class RatingRowState(
    val label: String,
    val count: Int,
    val maxProgress: Int,
    val colorHex: String
)
