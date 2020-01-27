package com.echsylon.komoot.screens.main

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.echsylon.komoot.R

/**
 * This dialog shows a message, explaining to the user why the location
 * permissions will be requested. The caller is responsible for passing
 * a suitable action to take when the user taps the OK button.
 */
class LocationRationaleDialog(private val onOk: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity
            ?.let { createDialog(it) }
            ?: throw IllegalStateException("Activity must not be null")
    }

    private fun createDialog(activity: FragmentActivity): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle(R.string.i_can_explain)
            .setMessage(R.string.the_app_wants_to_track_your_route)
            .setPositiveButton(R.string.ok) { _, _ -> onOk.invoke() }
            .create()
    }

}