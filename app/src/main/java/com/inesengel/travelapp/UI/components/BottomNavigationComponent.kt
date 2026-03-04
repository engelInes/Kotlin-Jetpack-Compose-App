package com.inesengel.travelapp.UI.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ADD_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.FAVORITES_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.HOME_INDEX

@Composable
fun BottomNavigationContainer(
    navController: NavController,
    onAddDestination: () -> Unit
) {
    var selectedNavItem by remember { mutableIntStateOf(HOME_INDEX) }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedNavItem = when (destination.id) {
                R.id.main_destination_fragment -> HOME_INDEX
                R.id.favorites_fragment -> FAVORITES_INDEX
                else -> selectedNavItem
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
    Column {
        HorizontalDivider(
            thickness = dimensionResource(R.dimen.small_thickness),
            color = colorResource(R.color.md_theme_outlineVariant)
        )
        BottomNavigationBar(
            selectedIndex = selectedNavItem,
            onItemSelected = { index ->
                when (index) {
                    HOME_INDEX ->
                        navController.popBackStack(
                            R.id.main_destination_fragment,
                            false
                        )

                    ADD_INDEX ->
                        onAddDestination()

                    FAVORITES_INDEX ->
                        navController.navigate(R.id.favorites_fragment)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationContainerPreview() {
    val navController = rememberNavController()

    BottomNavigationContainer(
        navController = navController,
        onAddDestination = {}
    )
}
