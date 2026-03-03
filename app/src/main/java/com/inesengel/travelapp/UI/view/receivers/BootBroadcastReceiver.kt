package com.inesengel.travelapp.UI.view.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.BOOT_ACTION
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.BOOT_TAG

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        if (intent?.action == BOOT_ACTION) {
            Log.d(BOOT_TAG, "boot completed")
            Toast.makeText(context, "Boot completed", Toast.LENGTH_LONG).show()
        }
    }
}
