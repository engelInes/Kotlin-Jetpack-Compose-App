package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.UI.view.utils.Constants.Geocoder.DEFAULT_LAT
import com.inesengel.travelapp.UI.view.utils.Constants.Geocoder.DEFAULT_LONG
import com.inesengel.travelapp.core.model.MapLocation
import com.inesengel.travelapp.core.model.TravelDestinationWithDetails
import com.inesengel.travelapp.core.usecase.GetCoordinatesFromAddressUseCase
import com.inesengel.travelapp.core.usecase.GetDestinationByIdUseCase
import com.inesengel.travelapp.core.usecase.UpdateDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationDetailViewModel @Inject constructor(
    private val getDestinationByIdUseCase: GetDestinationByIdUseCase,
    private val getCoordinatesFromAddressUseCase: GetCoordinatesFromAddressUseCase,
    private val updateDestinationUseCase: UpdateDestinationUseCase
) : ViewModel() {
    private val _destination = MutableStateFlow<TravelDestinationWithDetails?>(null)
    val destination: StateFlow<TravelDestinationWithDetails?> = _destination

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mapLocation = MutableStateFlow<MapLocation?>(null)
    val mapLocation: StateFlow<MapLocation?> = _mapLocation

    fun loadDestination(destinationId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = getDestinationByIdUseCase(destinationId).first()
            _destination.value = result

            result?.destination?.let { dest ->
                if (dest.latitude != DEFAULT_LAT || dest.longitude != DEFAULT_LONG) {
                    _mapLocation.value = MapLocation(
                        lat = dest.latitude,
                        lng = dest.longitude,
                        title = dest.name
                    )
                } else {
                    val address = "${dest.name}, ${dest.country}"
                    val fetchedLocation = getCoordinatesFromAddressUseCase(address)

                    if (fetchedLocation != null) {
                        _mapLocation.value = fetchedLocation

                        val updatedDestination = dest.copy(
                            latitude = fetchedLocation.lat,
                            longitude = fetchedLocation.lng
                        )
                        updateDestinationUseCase(updatedDestination)
                    }
                }
            }
            _isLoading.value = false
        }
    }
}
