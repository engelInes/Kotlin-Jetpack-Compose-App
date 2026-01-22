package com.inesengel.travelapp.UI.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.inesengel.travelapp.R

@Composable
fun SearchSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    val textColor = colorResource(R.color.md_theme_onPrimaryFixed)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(R.dimen.padding_standard)
            ),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth(0.95f),

            shape = RoundedCornerShape(dimensionResource(R.dimen.high_corner_radius)),

            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily(Font(R.font.be_vietnam_pro_light)),
                fontSize = dimensionResource(R.dimen.text_medium).value.sp,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
                color = textColor
            ),

            placeholder = {
                Text(
                    text = stringResource(R.string.search_destinations),
                    fontSize = dimensionResource(R.dimen.text_medium).value.sp,
                    color = textColor.copy(alpha = 0.6f)
                )
            },

            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = colorResource(R.color.md_theme_surfaceContainer_mediumContrast),
                focusedContainerColor = colorResource(R.color.md_theme_surfaceContainerHigh_mediumContrast),
                unfocusedBorderColor = colorResource(R.color.md_theme_outlineVariant_highContrast),
                focusedBorderColor = textColor
            ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_search),
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small_size)),
                    tint = textColor
                )
            }
        )
    }
}

@Composable
fun EmptyStateView(onAddDestination: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_large)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onAddDestination,
            modifier = Modifier.alpha(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.md_theme_surfaceVariant),
                contentColor = colorResource(R.color.md_theme_onSurfaceVariant)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_24px),
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.empty_list_message),
                    fontFamily = FontFamily(Font(R.font.aboreto_regular))
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_destination_title)) },
        text = { Text(stringResource(R.string.delete_destination_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete_yes_option))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.delete_no_option))
            }
        },
        containerColor = colorResource(R.color.md_theme_surfaceContainerHigh_highContrast),
        titleContentColor = colorResource(R.color.md_theme_onPrimaryFixed),
        textContentColor = colorResource(R.color.md_theme_onPrimaryFixed)
    )
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = colorResource(R.color.md_theme_primary_mediumContrast))
    }
}