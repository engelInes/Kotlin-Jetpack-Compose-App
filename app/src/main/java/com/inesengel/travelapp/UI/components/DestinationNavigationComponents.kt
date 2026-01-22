package com.inesengel.travelapp.UI.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ADD_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.FAVORITES_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.HOME_INDEX

@Composable
fun AppDrawer(
    userName: String,
    userEmail: String,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit
) {
    val headerBackground = colorResource(R.color.md_theme_surface)
    val headerText = colorResource(R.color.md_theme_onPrimaryFixed)
    val drawerBackground = colorResource(R.color.md_theme_surface)
    val iconColor = colorResource(R.color.md_theme_onPrimaryFixed)
    val textColor = colorResource(R.color.md_theme_onPrimaryFixed)
    val dividerColor = colorResource(R.color.md_theme_outlineVariant)
    val errorColor = colorResource(R.color.md_theme_error)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(dimensionResource(R.dimen.high_width))
            .background(drawerBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.box_height))
                .background(headerBackground)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(
                        start = dimensionResource(R.dimen.column_standard_padding),
                        top = dimensionResource(R.dimen.medium_height)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.box_size))
                        .clip(CircleShape)
                        .background(colorResource(R.color.md_theme_surfaceVariant)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.user),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_large_size))
                    )
                }

                Text(
                    text = userName.ifEmpty { stringResource(R.string.user_profile_label) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.be_vietnam_pro_light)),
                    color = headerText,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding_small),
                        top = dimensionResource(R.dimen.padding_standard)
                    )
                )

                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily(Font(R.font.be_vietnam_pro_light)),
                    color = headerText.copy(alpha = 0.8f),
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small))
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_standard))
                .weight(1f)
        ) {
            NavigationDrawerItem(
                label = {
                    Text(
                        text = stringResource(R.string.user_profile_label),
                        color = textColor,
                        fontFamily = FontFamily(Font(R.font.be_vietnam_pro_light))
                    )
                },
                selected = false,
                onClick = {
                    onClose()
                    onProfileClick()
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.edit_info_icon),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(dimensionResource(R.dimen.selected_icon_normal_size))
                    )
                },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.icon_small_padding))
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 0.dp),
            thickness = dimensionResource(R.dimen.small_thickness),
            color = dividerColor
        )

        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(R.string.bottom_nav_logout_text),
                    color = textColor,
                    fontFamily = FontFamily(Font(R.font.be_vietnam_pro_light))
                )
            },
            selected = false,
            onClick = {
                onClose()
                onLogout()
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.logout_24px),
                    contentDescription = null,
                    tint = errorColor
                )
            },
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.icon_small_padding),
                end = dimensionResource(R.dimen.icon_small_padding),
                top = dimensionResource(R.dimen.padding_small),
                bottom = dimensionResource(R.dimen.margin_bottom_large)
            )
        )
    }
}

@Composable
fun MainTopBar(
    title: String,
    onBroadcastClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val primaryTextColor = colorResource(R.color.md_theme_onPrimaryFixed)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.padding_standard),
                end = dimensionResource(R.dimen.padding_standard),
                top = dimensionResource(R.dimen.padding_extra_large),
                bottom = dimensionResource(R.dimen.padding_small)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = dimensionResource(R.dimen.text_extra_large).value.sp,
            color = primaryTextColor,
            fontFamily = FontFamily(Font(R.font.be_vietnam_pro_medium))
        )

        IconButton(onClick = onProfileClick) {
            Icon(
                painter = painterResource(R.drawable.menu_24px),
                contentDescription = stringResource(R.string.bottom_nav_profile_text),
                tint = primaryTextColor
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val barBackgroundColor = colorResource(R.color.md_theme_surface)
    val selectedColor = colorResource(R.color.md_theme_onPrimaryFixed)
    val unselectedColor = colorResource(R.color.md_theme_surfaceContainerLowest_highContrast)
    val iconSizeLarge = dimensionResource(R.dimen.selected_icon_size_large)
    val iconSizeNormal = dimensionResource(R.dimen.selected_icon_normal_size)

    NavigationBar(
        containerColor = barBackgroundColor,
        tonalElevation = 0.dp
    ) {
        fun iconSize(isSelected: Boolean) =
            if (isSelected) iconSizeLarge else iconSizeNormal

        fun iconColor(isSelected: Boolean) =
            if (isSelected) selectedColor else unselectedColor

        NavigationBarItem(
            selected = selectedIndex == HOME_INDEX,
            onClick = { onItemSelected(HOME_INDEX) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            ),
            icon = {
                Icon(
                    painter = painterResource(R.drawable.icons8_home),
                    contentDescription = stringResource(R.string.bottom_nav_home_text),
                    tint = iconColor(selectedIndex == HOME_INDEX),
                    modifier = Modifier.size(iconSize(selectedIndex == HOME_INDEX))
                )
            },
            label = {
                Text(
                    stringResource(R.string.bottom_nav_home_text),
                    fontSize = dimensionResource(R.dimen.bottom_nav_text_size).value.sp,
                    color = iconColor(selectedIndex == HOME_INDEX)
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = { onItemSelected(ADD_INDEX) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            ),
            icon = {
                Icon(
                    painter = painterResource(R.drawable.add_24px),
                    contentDescription = stringResource(R.string.add_button_name),
                    tint = colorResource(R.color.md_theme_onPrimary),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.small_modifier_size))
                        .background(
                            color = selectedColor,
                            shape = CircleShape
                        )
                        .padding(dimensionResource(R.dimen.icon_padding_small))
                )
            },
            label = {
                Text(
                    stringResource(R.string.add_button_name),
                    fontSize = dimensionResource(R.dimen.bottom_nav_text_size).value.sp,
                    color = unselectedColor
                )
            }
        )

        NavigationBarItem(
            selected = selectedIndex == FAVORITES_INDEX,
            onClick = { onItemSelected(FAVORITES_INDEX) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            ),
            icon = {
                Icon(
                    painter = painterResource(R.drawable.favorite_24px),
                    contentDescription = stringResource(R.string.bottom_nav_favorites_text),
                    tint = iconColor(selectedIndex == FAVORITES_INDEX),
                    modifier = Modifier.size(iconSize(selectedIndex == FAVORITES_INDEX))
                )
            },
            label = {
                Text(
                    stringResource(R.string.bottom_nav_favorites_text),
                    fontSize = dimensionResource(R.dimen.bottom_nav_text_size).value.sp,
                    color = iconColor(selectedIndex == FAVORITES_INDEX)
                )
            }
        )
    }
}