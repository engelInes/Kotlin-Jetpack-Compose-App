package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.UI.model.MainDestinationUiState
import com.inesengel.travelapp.core.usecase.DeleteDestinationUseCase
import com.inesengel.travelapp.core.usecase.GetDestinationsUseCase
import com.inesengel.travelapp.core.usecase.GetEmailUseCase
import com.inesengel.travelapp.core.usecase.GetUsernameUseCase
import com.inesengel.travelapp.core.usecase.LogoutUserUseCase
import com.inesengel.travelapp.core.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import project.model.TravelType
import javax.inject.Inject

@HiltViewModel
class MainDestinationListViewModel @Inject constructor(
    private val getDestinationsUseCase: GetDestinationsUseCase,
    private val deleteDestinationUseCase: DeleteDestinationUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getUsernameUseCase: GetUsernameUseCase,
    private val getEmailUseCase: GetEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainDestinationUiState())
    val uiState: StateFlow<MainDestinationUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")

    private val _selectedCategory = MutableStateFlow<TravelType?>(null)

    init {
        loadUserInfo()
    }

    fun loadDestinations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                _searchQuery,
                _selectedCategory
            ) { query, type ->
                query to type
            }.flatMapLatest { (query, type) ->
                getDestinationsUseCase(query, type)
            }.catch {
                _uiState.update { it.copy(isLoading = false) }
            }.collect { destinations ->
                _uiState.update {
                    it.copy(
                        destinations = destinations,
                        selectedType = _selectedCategory.value,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onToggleFavorite(destinationId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(
                destinationId,
                true
            )
            onCancelMenu()
        }
    }

    fun onLogoutUser() {
        viewModelScope.launch {
            logoutUserUseCase().collect {
                _uiState.update { it.copy(logout = true) }
            }
        }
    }

    fun onTravelTypeSelected(type: TravelType?) {
        _selectedCategory.value = type
        _uiState.update { it.copy(selectedType = type) }
    }

    fun consumeLogoutEvent() {
        _uiState.update { it.copy(logout = false) }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onDestinationLongPress(destinationId: Int) {
        _uiState.update { it.copy(destinationOptionsMenu = destinationId) }
    }

    fun onCancelMenu() {
        _uiState.update { it.copy(destinationOptionsMenu = null) }
    }

    fun onDeleteOptionClicked() {
        _uiState.update { it.copy(showDeleteMessage = true) }
    }

    fun onCancelDeleteOption() {
        _uiState.update {
            it.copy(
                showDeleteMessage = false,
                destinationOptionsMenu = null
            )
        }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            val id = _uiState.value.destinationOptionsMenu ?: return@launch

            val deleted = deleteDestinationUseCase(id)

            if (deleted) {
                _uiState.update {
                    it.copy(
                        showDeleteMessage = false,
                        destinationOptionsMenu = null
                    )
                }
            }
        }
    }

    fun refreshUserProfile() {
        viewModelScope.launch {
            val name = getUsernameUseCase().first()
            val email = getEmailUseCase().first()

            _uiState.update {
                it.copy(
                    userName = name,
                    userEmail = email
                )
            }
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            combine(
                getUsernameUseCase(),
                getEmailUseCase()
            ) { name, email ->
                name to email
            }.collect { (name, email) ->
                _uiState.update {
                    it.copy(
                        userName = name,
                        userEmail = email
                    )
                }
            }
        }
    }
}