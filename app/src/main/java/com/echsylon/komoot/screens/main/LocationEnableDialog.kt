package com.echsylon.komoot.screens.main

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.echsylon.komoot.R

/**
 * This dialog shows a message, informing the user that (s)he needs to enable
 * location services in the device's Settings. The caller is responsible for
 * passing a suitable action to take when the user taps the OK button.
 */
class LocationEnableDialog(private val okAction: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity
            ?.let { createDialog(it) }
            ?: throw IllegalStateException("Activity must not be null")
    }

    private fun createDialog(activity: FragmentActivity): Dialog {
        return AlertDialog.Builder(activity)
            .setMessage(R.string.enable_location_services)
            .setPositiveButton(R.string.go) { _, _ -> okAction.invoke() }
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
            .create()
    }

}