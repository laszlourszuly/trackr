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

/**
 * Enables the tracking flag if not already enabled.
 *
 * @return Boolean true if the flag was changed, else false.
 */
fun SharedPreferences.optTracking(): Boolean {
    // There is a theoretical TOCTOU in this method
    // due to the non-atomic state change.
    var didChange = false
    if (isNotTracking()) {
        didChange = true
        setTracking()
    }
    return didChange
}

/**
 * Disables the tracking flag if not already disabled.
 *
 * @return Boolean true if the flag was changed, else false.
 */
fun SharedPreferences.optNotTracking(): Boolean {
    // There is a theoretical TOCTOU in this method
    // due to the non-atomic state change.
    var didChange = false
    if (isTracking()) {
        didChange = true
        setNotTracking()
    }
    return didChange
}
