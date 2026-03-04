package com.inesengel.travelapp.UI.view.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.WHITESPACE_REGEX
import java.io.File
import java.io.FileOutputStream

fun Activity.navigateTo(destination: Class<*>, finish: Boolean = true) {
    val intent = Intent(this, destination)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    if (finish) finish()
}

fun String.toTitleCase(): String {
    return this.trim().split(WHITESPACE_REGEX.toRegex())
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}

fun copyToInternalStorage(
    context: Context,
    uri: Uri,
    fileName: String
): String {
    val file = File(context.filesDir, fileName)

    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}
