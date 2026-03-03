package com.inesengel.travelapp.UI.view.utils

object Constants {
    object Validation {
        const val ONE_DIGIT_REGEX = ".*[0-9].*"
        const val ONE_SPECIAL_CHARACTER_REGEX = ".*[!@#\$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?-].*"
        const val ONE_LOWERCASE_CHARACTER_REGEX = ".*[a-z].*"
        const val ONE_UPPERCASE_CHARACTER_REGEX = ".*[A-Z].*"
        const val WHITESPACE_REGEX = "\\s+"
        const val CHARACTERS_REGEX = "^[a-zA-Z ]+$"
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val IMAGE_PICKER_FORMAT = "image/*"
        const val SUCCESS_EMIT_MESSAGE = "Success"
        const val PASSWORD_LENGTH = 8
    }

    object Navigation {
        const val ARG_DESTINATION_ID = "destination_id"
        const val ARG_DESTINATION_NAME = "destination_name"
        const val INVALID_DESTINATION_ID = -1
        const val NULL_INDEX = 0
        const val DESTINATION_DETAILS_INDEX = 0
        const val ADD_DESTINATION_ATTRACTION_INDEX = 1
        const val ADD_DESTINATION_REVIEWS_INDEX = 2
        const val ADD_FORM_PROGRESS = 33
        const val HOME_INDEX = 0
        const val FAVORITES_INDEX = 1
        const val ADD_INDEX = 2
        const val INITIAL_PAGE_INDICATOR_COUNT = 0
        const val SINGLE_PAGE_INDICATOR = 1
        const val DEFAULT_STEP_INDEX = 1
        const val INITIAL_PAGE_INDICATOR_INDEX = 0
    }

    object Notification {
        const val OTHER_APP_NOTIFICATION_NAME = "Notification from other app"
        const val OTHER_APP_NOTIFICATION_DESCRIPTION = "Notifications from other app broadcasts"
    }

    object UIViews {
        const val DESTINATION_DETAILS_ATTRACTION_INDEX = 0
        const val DESTINATION_DETAILS_REVIEW_INDEX = 1
        const val NOTIFICATION_TAG = "Notifications"
        const val GOOGLE_MAPS_TAG = "MAP"
    }

    object Broadcasts {
        const val MY_CUSTOM_ACTION = "com.inesengel.travelapp.ACTION_SHOW_ENTITIES"
        const val MY_TRAVEL_PERMISSION = "com.inesengel.travelapp.permission.TRAVEL_PERMISSION"
        const val BROADCAST_TAG = "Broadcast"
        const val BATTERY_CHANGED_ACTION = "android.intent.action.BATTERY_CHANGED"
        const val START_DELAY_COMMAND = "START_DELAY"
        const val CANCEL_DELAY_COMMAND = "CANCEL_DELAY"
        const val BATTERY_TAG = "battery"
        const val BOOT_TAG = "boot"
        const val BOOT_ACTION = "android.intent.action.BOOT_COMPLETED"
        const val RECEIVER_TAG = "receiver"
        const val COMPONENT_RECEIVER =
            "com.annaaa.plantstoreapp.application.receivers.DisplayPlantsReceiver"
        const val COMPONENT_PACKAGE_NAME = "com.annaaa.plantstoreapp"
        const val PLANTS_ACTION = "com.annaaa.plantstoreapp.action.PLANTS"
        const val USER_TRACKING_COMMAND = "COMMAND"
    }

    object Duration {
        const val LOADING_BAR_ANIMATION_VISIBILITY_MS = 300
        const val CONTENT_ANIMATION_LOADING_DURATION_MS = 500
        const val CONTENT_ANIMATION_ITEM_DELAY_MS = 100
    }

    object ReviewConstants {

        object ReviewLabels {
            const val EXCELLENT = "Excellent"
            const val GOOD = "Good"
            const val AVERAGE = "Average"
            const val BELOW_AVERAGE = "Below Average"
            const val POOR = "Poor"
        }

        object Colors {
            const val GREEN = "#4CAF50"
            const val LIGHT_GREEN = "#8BC34A"
            const val YELLOW = "#FFEB3B"
            const val ORANGE = "#FF9800"
            const val RED = "#F44336"
        }

        const val NULL_COUNT = 0
    }

    object Orientation {
        const val HORIZONTAL_MARGIN = 8
        const val VERTICAL_MARGIN = 0
    }

    object Geocoder{
        const val DEFAULT_LAT = 0.0
        const val DEFAULT_LONG = 0.0
    }
}
