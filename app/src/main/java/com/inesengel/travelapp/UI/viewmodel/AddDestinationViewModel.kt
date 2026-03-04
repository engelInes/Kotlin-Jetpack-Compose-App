package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.UI.model.NavigationState
import com.inesengel.travelapp.UI.view.utils.Constants.Geocoder.DEFAULT_LAT
import com.inesengel.travelapp.UI.view.utils.Constants.Geocoder.DEFAULT_LONG
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.NULL_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.SUCCESS_EMIT_MESSAGE
import com.inesengel.travelapp.UI.view.utils.toTitleCase
import com.inesengel.travelapp.core.model.AddDestinationFormState
import com.inesengel.travelapp.core.model.DestinationAttraction
import com.inesengel.travelapp.core.model.ResultState
import com.inesengel.travelapp.core.model.UserReview
import com.inesengel.travelapp.core.usecase.AddDestinationUseCase
import com.inesengel.travelapp.core.usecase.GetCoordinatesFromAddressUseCase
import com.inesengel.travelapp.core.usecase.GetDestinationByIdUseCase
import com.inesengel.travelapp.core.usecase.GetUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import project.model.TravelDestination
import project.model.TravelType
import javax.inject.Inject

@HiltViewModel
class AddDestinationViewModel @Inject constructor(
    private val addDestinationUseCase: AddDestinationUseCase,
    private val getDestinationByIdUseCase: GetDestinationByIdUseCase,
    private val getUsernameUseCase: GetUsernameUseCase,
    private val getCoordinatesFromAddressUseCase: GetCoordinatesFromAddressUseCase
) : ViewModel() {
    private val _name = MutableStateFlow("")
    private val _type = MutableStateFlow(TravelType.ADVENTURE)
    private val _country = MutableStateFlow("")
    private val _price = MutableStateFlow("")
    private val _duration = MutableStateFlow("")
    private val _attractions = MutableStateFlow<List<DestinationAttraction>>(emptyList())
    private val _reviews = MutableStateFlow<List<UserReview>>(emptyList())
    private var currentEditingId: Int? = null

    private val _addResult = MutableSharedFlow<ResultState>()
    val addResult: SharedFlow<ResultState> = _addResult

    private val _currentUserName = MutableStateFlow("")
    val currentUserName: StateFlow<String> = _currentUserName

    private val _navigationState = MutableSharedFlow<NavigationState>()
    val navigationState: SharedFlow<NavigationState> = _navigationState

    val formState: StateFlow<AddDestinationFormState> = combine(
        listOf(
            _name,
            _country,
            _price,
            _duration,
            _attractions,
            _reviews,
            _type
        )
    ) { values ->
        val name = values[0] as String
        val country = values[1] as String
        val price = values[2] as String
        val duration = values[3] as String
        val attractions = values[4] as List<DestinationAttraction>
        val reviews = values[5] as List<UserReview>
        val type = values[6] as TravelType

        AddDestinationFormState(
            name = name,
            type = type,
            country = country,
            price = price,
            duration = duration,
            attractions = attractions,
            reviews = reviews,
            isDataValid = name.isNotBlank() &&
                    country.isNotBlank() &&
                    price.toDoubleOrNull() != null &&
                    duration.toIntOrNull() != null
        )
    }.stateIn(
        scope = viewModelScope + Dispatchers.IO,
        started = SharingStarted.Lazily,
        initialValue = AddDestinationFormState()
    )

    init {
        viewModelScope.launch {
            getUsernameUseCase().collect { username ->
                _currentUserName.value = username
            }
        }
    }

    fun onNameChanged(value: String) {
        _name.value = value
    }

    fun onCountryChanged(value: String) {
        _country.value = value
    }

    fun onTypeChanged(value: TravelType) {
        _type.value = value
    }

    fun onPriceChanged(value: String) {
        _price.value = value
    }

    fun onDurationChanged(value: String) {
        _duration.value = value
    }

    fun addAttraction(attraction: DestinationAttraction) {
        _attractions.value += attraction
    }

    fun addReview(review: UserReview) {
        _reviews.value += review
    }

    fun saveDestination() {
        viewModelScope.launch {
            val currentState = formState.value

            if (!currentState.isDataValid) return@launch
            val coverAttraction = currentState.attractions.randomOrNull()

            val imageResId = coverAttraction?.imageResId ?: NULL_INDEX
            val imagePath = coverAttraction?.imagePath

            val formattedName = currentState.name.toTitleCase()
            val formattedCountry = currentState.country.toTitleCase()

            val addressSearch = "$formattedName, $formattedCountry"
            val mapLocation = getCoordinatesFromAddressUseCase(addressSearch)

            val lat = mapLocation?.lat ?: DEFAULT_LAT
            val lng = mapLocation?.lng ?: DEFAULT_LONG

            val destination = TravelDestination(
                id = currentEditingId ?: NULL_INDEX,
                name = formattedName,
                type = currentState.type,
                country = formattedCountry,
                price = currentState.price.toDouble(),
                duration = currentState.duration.toInt(),
                visited = false,
                rating = 0,
                imageResId = imageResId,
                imagePath = imagePath,
                latitude = lat,
                longitude = lng
            )

            addDestinationUseCase(
                destination = destination,
                attractions = currentState.attractions,
                reviews = currentState.reviews
            )

            _addResult.emit(ResultState.Success(SUCCESS_EMIT_MESSAGE))
            _navigationState.emit(NavigationState.GO_TO_MAIN)
        }
    }

    fun removeAttraction(attraction: DestinationAttraction) {
        val currentList = _attractions.value.toMutableList()
        currentList.remove(attraction)
        _attractions.value = currentList
    }

    fun loadDestinationForEdit(id: Int) {
        currentEditingId = id

        viewModelScope.launch {
            getDestinationByIdUseCase(id).collect { destinationWithDetails ->
                destinationWithDetails ?: return@collect

                val destination = destinationWithDetails.destination
                _name.value = destination.name
                _country.value = destination.country
                _price.value = destination.price.toString()
                _duration.value = destination.duration.toString()
                _type.value = destination.type
                _attractions.value = destinationWithDetails.attractions
                _reviews.value = destinationWithDetails.reviews
            }
        }
    }

    fun addAttractionObject(attraction: DestinationAttraction) {
        if (attraction.name.isBlank()) {
            return
        }
        addAttraction(attraction)
    }
}
