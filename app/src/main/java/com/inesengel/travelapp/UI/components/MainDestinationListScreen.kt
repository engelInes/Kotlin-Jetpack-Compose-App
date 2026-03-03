package com.inesengel.travelapp.UI.componentsimport

import androidx.compose.animation.AnimatedVisibility
import com.inesengel.travelapp.UI.components.AppDrawer
import com.inesengel.travelapp.UI.components.DeleteConfirmationDialog
import com.inesengel.travelapp.UI.components.DestinationList
import com.inesengel.travelapp.UI.components.EmptyStateView
import com.inesengel.travelapp.UI.components.LoadingOverlay
import com.inesengel.travelapp.UI.components.MainTopBar
import com.inesengel.travelapp.UI.components.SearchSection
import com.inesengel.travelapp.UI.components.TravelTypeSelector

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.model.MainDestinationUiState
import com.inesengel.travelapp.UI.view.utils.Constants.Duration.LOADING_BAR_ANIMATION_VISIBILITY_MS
import com.inesengel.travelapp.UI.viewmodel.MainDestinationListViewModel
import kotlinx.coroutines.launch
import project.model.TravelDestination
import project.model.TravelType

@Composable
fun MainDestinationListScreen(
    viewModel: MainDestinationListViewModel,
    onDestinationClicked: (Int, String) -> Unit,
    onSendBroadcast: () -> Unit,
    onLogout: () -> Unit,
    onAddDestination: () -> Unit,
    onUpdate: () -> Unit,
    onFavoritesClicked: () -> Unit,
    onShareClicked: (TravelDestination) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshUserProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = uiState.logout) {
        if (uiState.logout) {
            onLogout()
            viewModel.consumeLogoutEvent()
        }
    }
    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.md_theme_surface)
    ) {
        MainDestinationListContent(
            uiState = uiState,
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
            onDestinationClicked = onDestinationClicked,
            onSendBroadcast = onSendBroadcast,
            onLogout = viewModel::onLogoutUser,
            onTravelTypeSelected = viewModel::onTravelTypeSelected,
            onDestinationLongPress = viewModel::onDestinationLongPress,
            onCancelMenu = viewModel::onCancelMenu,
            onDeleteOptionClicked = viewModel::onDeleteOptionClicked,
            onConfirmDelete = viewModel::onConfirmDelete,
            onCancelDeleteOption = viewModel::onCancelDeleteOption,
            onAddDestination = onAddDestination,
            onFavoriteOptionClicked = viewModel::onToggleFavorite,
            onUpdate = onUpdate,
            onShareClicked = onShareClicked,
            onNavigateToProfile = onNavigateToProfile
        )
    }
}

@Composable
private fun MainDestinationListContent(
    uiState: MainDestinationUiState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onDestinationClicked: (Int, String) -> Unit,
    onSendBroadcast: () -> Unit,
    onLogout: () -> Unit,
    onTravelTypeSelected: (TravelType?) -> Unit,
    onDestinationLongPress: (Int) -> Unit,
    onCancelMenu: () -> Unit,
    onDeleteOptionClicked: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDeleteOption: () -> Unit,
    onAddDestination: () -> Unit,
    onFavoriteOptionClicked: (Int) -> Unit,
    onUpdate: () -> Unit,
    onShareClicked: (TravelDestination) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val isEmpty = uiState.destinations.isEmpty() && !uiState.isLoading

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                userName = uiState.userName,
                userEmail = uiState.userEmail,
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToProfile()
                },
                onLogout = onLogout,
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = colorResource(R.color.md_theme_surface),
            topBar = {
                MainTopBar(
                    title = stringResource(R.string.main_title),
                    onBroadcastClick = onSendBroadcast,
                    onProfileClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensionResource(R.dimen.padding_standard))
                        .alpha(if (uiState.destinationOptionsMenu != null) 0.3f else 1f)
                ) {
                    SearchSection(
                        searchQuery = searchQuery,
                        onSearchQueryChanged = onSearchQueryChanged
                    )
                    TravelTypeSelector(
                        types = TravelType.entries,
                        selectedType = uiState.selectedType,
                        onTypeSelected = onTravelTypeSelected
                    )
                    if (isEmpty) {
                        EmptyStateView(onAddDestination = onAddDestination)
                    } else {
                        DestinationList(
                            destinations = uiState.destinations,
                            activeMenuId = uiState.destinationOptionsMenu,
                            modifier = Modifier.weight(1f),
                            onDestinationClicked = onDestinationClicked,
                            onDestinationLongPress = onDestinationLongPress,
                            onFavoriteClicked = onFavoriteOptionClicked,
                            onShareClicked = onShareClicked,
                            onUpdate = onUpdate,
                            onDelete = onDeleteOptionClicked,
                            onCancelMenu = onCancelMenu
                        )
                    }
                }
                AnimatedVisibility(
                    visible = uiState.isLoading,
                    enter = fadeIn(animationSpec = tween(LOADING_BAR_ANIMATION_VISIBILITY_MS)),
                    exit = fadeOut(animationSpec = tween(LOADING_BAR_ANIMATION_VISIBILITY_MS))
                ) {
                    LoadingOverlay()
                }

                if (uiState.showDeleteMessage) {
                    DeleteConfirmationDialog(
                        onConfirm = onConfirmDelete,
                        onDismiss = onCancelDeleteOption
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DestinationListItemPreview() {
    val mockDestinations = listOf(
        TravelDestination(
            id = 1,
            name = "Weekend in Rome",
            type = TravelType.CITY_BREAK,
            price = 1200.00,
            rating = 5,
            country = "Italy",
            visited = false,
            duration = 3,
            imageResId = R.drawable.no_picture
        ),
        TravelDestination(
            id = 2,
            name = "Barcelona City break",
            type = TravelType.CITY_BREAK,
            price = 1200.00,
            rating = 5,
            country = "Spain",
            visited = false,
            duration = 3,
            imageResId = R.drawable.no_picture
        ),
        TravelDestination(
            id = 3,
            name = "Monaco Grand Prix",
            type = TravelType.CITY_BREAK,
            price = 1200.00,
            rating = 5,
            country = "Monaco",
            visited = false,
            duration = 3,
            imageResId = R.drawable.no_picture
        )
    )
    DestinationList(
        destinations = mockDestinations,
        onDestinationClicked = { _, _ -> },
        onDestinationLongPress = {},
        onShareClicked = {},
        onFavoriteClicked = {},
        activeMenuId = 0,
        onUpdate = {},
        onDelete = {},
        onCancelMenu = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun MainDestinationListContentPreview() {
    val mockUiState = MainDestinationUiState(
        destinations = listOf(
            TravelDestination(
                id = 1,
                name = "Weekend in Rome",
                country = "Italy",
                type = TravelType.CITY_BREAK,
                price = 1200.00,
                rating = 5,
                visited = false,
                duration = 3,
                imageResId = R.drawable.no_picture
            )
        ),
        isLoading = false,
        logout = false
    )

    MainDestinationListContent(
        uiState = mockUiState,
        searchQuery = "Barcelona",
        onSearchQueryChanged = {},
        onDestinationClicked = { _, _ -> },
        onSendBroadcast = {},
        onLogout = {},
        onTravelTypeSelected = {},
        onDestinationLongPress = {},
        onCancelMenu = {},
        onUpdate = {},
        onDeleteOptionClicked = {},
        onConfirmDelete = {},
        onCancelDeleteOption = {},
        onAddDestination = {},
        onFavoriteOptionClicked = {},
        onShareClicked = {},
        onNavigateToProfile = {}
    )
}
