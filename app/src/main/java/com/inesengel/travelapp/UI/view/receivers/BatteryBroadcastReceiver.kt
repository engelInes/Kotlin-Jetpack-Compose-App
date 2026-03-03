package com.inesengel.travelapp.UI.view.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.BATTERY_CHANGED_ACTION
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.BATTERY_TAG

class BatteryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        if (intent?.action == BATTERY_CHANGED_ACTION) {
            Log.d(BATTERY_TAG, "Battery level changed")
        }
    }
}
