package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.UI.model.PageIndicatorState
import com.inesengel.travelapp.UI.model.Rating
import com.inesengel.travelapp.UI.model.RatingCategory
import com.inesengel.travelapp.UI.model.RatingRowState
import com.inesengel.travelapp.UI.model.ReviewStats
import com.inesengel.travelapp.UI.model.toCategory
import com.inesengel.travelapp.UI.view.utils.Constants.ReviewConstants.ReviewLabels
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.INITIAL_PAGE_INDICATOR_COUNT
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.INITIAL_PAGE_INDICATOR_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.NULL_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.ReviewConstants.Colors.GREEN
import com.inesengel.travelapp.UI.view.utils.Constants.ReviewConstants.Colors.LIGHT_GREEN
import com.inesengel.travelapp.UI.view.utils.Constants.ReviewConstants.Colors.ORANGE
import com.inesengel.travelapp.UI.view.utils.Constants.ReviewConstants.Colors.RED
import com.inesengel.travelapp.UI.view.utils.Constants.ReviewConstants.Colors.YELLOW
import com.inesengel.travelapp.UI.view.utils.Constants.ReviewConstants.NULL_COUNT
import com.inesengel.travelapp.core.model.DestinationAttraction
import com.inesengel.travelapp.core.model.UserReview
import com.inesengel.travelapp.core.usecase.GetAttractionsUseCase
import com.inesengel.travelapp.core.usecase.GetUserReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class DestinationTabViewModel @Inject constructor(
    getAttractionsUseCase: GetAttractionsUseCase,
    getUserReviewsUseCase: GetUserReviewsUseCase
) : ViewModel() {

    private val destinationIdState = MutableStateFlow<Int?>(null)

    private val _currentAttractionIndex = MutableStateFlow(NULL_INDEX)
    val currentAttractionIndex = _currentAttractionIndex.asStateFlow()

    val attractions: StateFlow<List<DestinationAttraction>> =
        destinationIdState
            .filterNotNull()
            .flatMapLatest { id ->
                getAttractionsUseCase(id)
            }
            .stateIn(
                scope = viewModelScope + Dispatchers.IO,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    val reviews: StateFlow<List<UserReview>> =
        destinationIdState
            .filterNotNull()
            .flatMapLatest { id ->
                getUserReviewsUseCase(id)
            }
            .stateIn(
                scope = viewModelScope + Dispatchers.IO,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    val reviewStats: StateFlow<ReviewStats> = reviews
        .map { list -> calculateStats(list) }
        .stateIn(
            scope = viewModelScope + Dispatchers.Default,
            started = SharingStarted.Lazily,
            initialValue = ReviewStats()
        )

    val pageIndicatorState: StateFlow<PageIndicatorState> =
        combine(
            attractions,
            currentAttractionIndex
        ) { list, index ->
            PageIndicatorState(
                count = list.size,
                activeIndex = index
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PageIndicatorState(
                INITIAL_PAGE_INDICATOR_COUNT,
                INITIAL_PAGE_INDICATOR_INDEX
            )
        )

    fun setDestinationId(id: Int) {
        if (destinationIdState.value != id) {
            destinationIdState.value = id
        }
    }

    fun onAttractionSwiped(index: Int) {
        if (_currentAttractionIndex.value != index) {
            _currentAttractionIndex.value = index
        }
    }

    private fun calculateStats(reviews: List<UserReview>): ReviewStats {
        if (reviews.isEmpty()) return ReviewStats()

        val total = reviews.size

        val average = reviews
            .map { it.rating.toDouble() }
            .average()
            .toBigDecimal()
            .setScale(1, RoundingMode.HALF_UP)
            .toFloat()

        val ratingCounts = reviews
            .groupingBy { it.rating.toInt() }
            .eachCount()

        val rows = Rating.values().map { rating ->
            val count = ratingCounts[rating.value] ?: NULL_COUNT
            val category = rating.toCategory()

            RatingRowState(
                label = labelForCategory(category),
                count = count,
                maxProgress = total,
                colorHex = colorForCategory(category)
            )
        }

        return ReviewStats(
            averageRating = average,
            totalReviews = total,
            ratingRows = rows
        )
    }

    private fun colorForCategory(category: RatingCategory): String =
        when (category) {
            RatingCategory.EXCELLENT -> GREEN
            RatingCategory.GOOD -> LIGHT_GREEN
            RatingCategory.AVERAGE -> YELLOW
            RatingCategory.BELOW_AVERAGE -> ORANGE
            RatingCategory.POOR -> RED
        }

    private fun labelForCategory(category: RatingCategory): String =
        when (category) {
            RatingCategory.EXCELLENT -> ReviewLabels.EXCELLENT
            RatingCategory.GOOD -> ReviewLabels.GOOD
            RatingCategory.AVERAGE -> ReviewLabels.AVERAGE
            RatingCategory.BELOW_AVERAGE -> ReviewLabels.BELOW_AVERAGE
            RatingCategory.POOR -> ReviewLabels.POOR
        }
}
