package com.inesengel.travelapp.UI.model

enum class Rating(val value: Int) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5)
}

enum class RatingCategory {
    EXCELLENT,
    GOOD,
    AVERAGE,
    BELOW_AVERAGE,
    POOR
}

fun Rating.toCategory(): RatingCategory =
    when (this) {
        Rating.FIVE -> RatingCategory.EXCELLENT
        Rating.FOUR -> RatingCategory.GOOD
        Rating.THREE -> RatingCategory.AVERAGE
        Rating.TWO -> RatingCategory.BELOW_AVERAGE
        Rating.ONE -> RatingCategory.POOR
    }
