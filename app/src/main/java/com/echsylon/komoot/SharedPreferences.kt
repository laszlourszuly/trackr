package com.echsylon.komoot

import android.content.SharedPreferences
import androidx.core.content.edit

fun SharedPreferences.isTracking(): Boolean {
    return getBoolean("tracking", false)
}

fun SharedPreferences.isNotTracking(): Boolean {
    return !isTracking()
}

fun SharedPreferences.setTracking() {
    edit() { putBoolean("tracking", true) }
}

fun SharedPreferences.setNotTracking() {
    edit() { putBoolean("tracking", false) }
}

