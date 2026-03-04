package com.inesengel.travelapp.core.model

import com.inesengel.travelapp.UI.model.RatingRowState
import com.inesengel.travelapp.UI.model.ReviewStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewStatsTest {

    @Test
    fun `default constructor values are correct`() {
        val stats = ReviewStats()

        assertEquals(0f, stats.averageRating, 0.0f)
        assertEquals(0, stats.totalReviews)
        assertTrue(stats.ratingRows.isEmpty())
    }

    @Test
    fun `custom values are set correctly`() {
        val rows = listOf(
            RatingRowState("5 stars", 10, 10, "#00FF00"),
            RatingRowState("4 stars", 5, 10, "#FFFF00")
        )

        val stats = ReviewStats(
            averageRating = 4.5f,
            totalReviews = 15,
            ratingRows = rows
        )

        assertEquals(4.5f, stats.averageRating, 0.0f)
        assertEquals(15, stats.totalReviews)
        assertEquals(2, stats.ratingRows.size)
        assertEquals("5 stars", stats.ratingRows[0].label)
        assertEquals(10, stats.ratingRows[0].count)
        assertEquals(10, stats.ratingRows[0].maxProgress)
        assertEquals("#00FF00", stats.ratingRows[0].colorHex)
    }

    @Test
    fun `ratingRows list is immutable`() {
        val stats = ReviewStats()
        assertTrue(stats.ratingRows.isEmpty())
    }
}
