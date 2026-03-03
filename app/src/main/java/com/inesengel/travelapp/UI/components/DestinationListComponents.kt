package com.inesengel.travelapp.UI.components

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import com.inesengel.travelapp.UI.view.utils.Constants.Duration.CONTENT_ANIMATION_ITEM_DELAY_MS
import com.inesengel.travelapp.UI.view.utils.Constants.Duration.CONTENT_ANIMATION_LOADING_DURATION_MS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import project.model.TravelDestination
import project.model.TravelType

@Composable
fun DestinationList(
    destinations: List<TravelDestination>,
    activeMenuId: Int?,
    modifier: Modifier = Modifier,
    onDestinationClicked: (Int, String) -> Unit,
    onDestinationLongPress: (Int) -> Unit,
    onFavoriteClicked: (Int) -> Unit,
    onShareClicked: (TravelDestination) -> Unit,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    onCancelMenu: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(id = R.dimen.padding_standard)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.padding_standard)
        )
    ) {
        itemsIndexed(
            destinations,
            key = { _, item -> item.id }
        ) { index, destination ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) { visible = true }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(
                    tween(
                        durationMillis = CONTENT_ANIMATION_LOADING_DURATION_MS,
                        delayMillis = index * CONTENT_ANIMATION_ITEM_DELAY_MS
                    )
                )
            ) {
                Box {
                    DestinationCard(
                        destination = destination,
                        onClick = { onDestinationClicked(destination.id, destination.name) },
                        onLongPress = { onDestinationLongPress(destination.id) },
                        onShareClicked = { onShareClicked(destination) },
                        onFavoriteClicked = { onFavoriteClicked(destination.id) }
                    )

                    if (activeMenuId == destination.id) {
                        DropdownMenu(
                            expanded = true,
                            onDismissRequest = onCancelMenu,
                            containerColor = colorResource(R.color.md_theme_surfaceContainerLow),
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = dimensionResource(id = R.dimen.default_card_elevation)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(R.string.update_button),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = colorResource(R.color.md_theme_onPrimaryFixed)
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.edit_item),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(dimensionResource(R.dimen.icon_small_size))
                                        )
                                    }
                                },
                                onClick = onUpdate
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_standard)),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(R.string.delete_button),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.delete_forever_24px),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                onClick = onDelete
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationCard(
    destination: TravelDestination,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onFavoriteClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(dimensionResource(R.dimen.destination_card_width))
            .height(dimensionResource(R.dimen.card_image_height))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongPress() }
                )
            },
        shape = RoundedCornerShape(dimensionResource(R.dimen.default_corner_radius)),
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.default_card_elevation)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box {
            val fileBitmap = rememberBitmapFromPath(destination.imagePath)

            if (fileBitmap != null) {
                Image(
                    bitmap = fileBitmap,
                    contentDescription = destination.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val imagePainter = if (destination.imageResId != 0) {
                    painterResource(id = destination.imageResId)
                } else {
                    painterResource(id = R.drawable.no_picture)
                }

                Image(
                    painter = imagePainter,
                    contentDescription = destination.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 200f
                        )
                    )
            )

            Icon(
                painter = painterResource(
                    id = if (destination.isFavorite) {
                        R.drawable.heart_solid_full
                    } else {
                        R.drawable.favorite_24px
                    }
                ),
                contentDescription = stringResource(R.string.bottom_nav_favorites_text),
                tint = if (destination.isFavorite) {
                    Color.Red
                } else {
                    Color.White
                },
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_standard))
                    .size(dimensionResource(R.dimen.padding_large))
                    .align(Alignment.TopStart)
                    .clickable { onFavoriteClicked() }
            )

            IconButton(
                onClick = onShareClicked,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_share),
                    contentDescription = stringResource(R.string.share_destination_intent_message),
                    tint = Color.White,
                    modifier = Modifier.size(dimensionResource(R.dimen.padding_large))
                )
            }

            Text(
                text = destination.name,
                color = Color.White,
                fontSize = dimensionResource(R.dimen.text_medium).value.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(dimensionResource(R.dimen.padding_standard))
            )
        }
    }
}

@Composable
fun TravelTypeSelector(
    types: List<TravelType>,
    selectedType: TravelType?,
    onTypeSelected: (TravelType?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.padding_large)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        item {
            CategoryChip(
                text = stringResource(R.string.all_text),
                selected = selectedType == null,
                onClick = { onTypeSelected(null) }
            )
        }

        items(types) { type ->
            CategoryChip(
                text = type.name.replace("_", " "),
                selected = selectedType == type,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val selectedContainerColor = colorResource(R.color.colorCustomColor2_highContrast)
    val unselectedContainerColor =
        colorResource(R.color.md_theme_surfaceContainerLow_mediumContrast)

    val selectedTextColor = colorResource(R.color.white)
    val unselectedTextColor = colorResource(R.color.md_theme_onPrimaryFixed)

    val borderColor = colorResource(R.color.md_theme_outlineVariant)
    Card(
        modifier = Modifier
            .wrapContentWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                selectedContainerColor
            } else {
                unselectedContainerColor
            }
        ),
        border = if (!selected) {
            BorderStroke(
                1.dp,
                borderColor
            )
        } else {
            null
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.padding_standard),
                vertical = dimensionResource(id = R.dimen.padding_small)
            ),
            color = if (selected) {
                selectedTextColor
            } else {
                unselectedTextColor
            },
            fontFamily = FontFamily(Font(R.font.be_vietnam_pro_light))
        )
    }
}

@Composable
fun rememberBitmapFromPath(path: String?): ImageBitmap? {
    var bitmap by remember(path) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(path) {
        if (path != null) {
            withContext(Dispatchers.IO) {
                try {
                    val decoded = BitmapFactory.decodeFile(path)
                    bitmap = decoded?.asImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                    bitmap = null
                }
            }
        }
    }
    return bitmap
}
