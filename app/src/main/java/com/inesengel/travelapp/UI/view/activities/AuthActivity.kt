package com.inesengel.travelapp.UI.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inesengel.travelapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)
    }
}
