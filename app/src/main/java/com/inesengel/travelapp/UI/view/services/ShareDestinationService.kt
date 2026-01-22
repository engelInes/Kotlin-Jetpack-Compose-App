package com.inesengel.travelapp.UI.view.services

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.inesengel.travelapp.R
import project.model.TravelDestination

class ShareDestinationService(private val context: Context) {

    fun shareDestination(destination: TravelDestination) {
        val shareText = context.getString(
            R.string.share_destination_text,
            destination.name,
            destination.country,
            destination.price
        )

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                shareText
            )
            type = context.getString(R.string.mime_text_plain)
        }

        val shareIntent = Intent.createChooser(
            sendIntent,
            context.getString(R.string.share_destination_intent_message)
        )
        ContextCompat.startActivity(context, shareIntent, null)
    }
}