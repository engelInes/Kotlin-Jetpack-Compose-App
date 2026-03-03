package com.inesengel.travelapp.infra.utils

object Constants {
    object DataSource {
        const val SHARED_PREFERENCES_FILE_NAME = "MyPreferences"
        const val RESULT_STATE_SUCCESS = "Success"
    }

    object Database {
        const val DATABASE_NAME = "travel_app_database"
        const val USER_REVIEW_TABLE_NAME = "reviews"
        const val ATTRACTIONS_TABLE_NAME = "attractions"
        const val DESTINATION_ID_KEY = "destinationId"
        const val PARENT_TABLE_ID_KEY = "id"
        const val DESTINATIONS_TABLE_NAME = "travel_destinations"
        const val DATABASE_VERSION = 6
        const val GEOCODER_MAX_RESULTS = 1
        const val KEY_DARK_MODE = "dark_mode"
    }
}
