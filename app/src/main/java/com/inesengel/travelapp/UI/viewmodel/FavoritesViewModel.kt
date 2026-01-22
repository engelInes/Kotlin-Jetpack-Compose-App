package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.core.usecase.GetFavoriteDestinationsUseCase
import com.inesengel.travelapp.core.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import project.model.TravelDestination
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoriteDestinationsUseCase: GetFavoriteDestinationsUseCase
) : ViewModel() {
    private val _favorites = MutableStateFlow<List<TravelDestination>>(emptyList())
    val favorites: StateFlow<List<TravelDestination>> = _favorites

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            getFavoriteDestinationsUseCase().collect { listWithDetails ->
                val destinationList = listWithDetails.map { it.destination }
                _favorites.value = destinationList
            }
        }
    }

    fun removeFromFavorites(destinationId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(
                id = destinationId,
                isFavorite = false
            )
        }
    }
}