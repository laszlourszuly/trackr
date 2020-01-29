package com.echsylon.komoot.screens.main

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import com.echsylon.komoot.location.LocationPermissionHelper
import com.echsylon.komoot.optNotTracking
import com.echsylon.komoot.optTracking
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.ArgumentMatchers.anyBoolean

class MainViewModelTestHelper {

    companion object {
        private var isTracking: Boolean? = null
        private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

        fun prepareLocationPermissionHelper(
            hasPermission: Boolean = true,
            isEnabled: Boolean = true
        ): LocationPermissionHelper {
            val mockedHelper: LocationPermissionHelper = mock()
            whenever(mockedHelper.hasLocationPermissions(any())).thenReturn(hasPermission)
            whenever(mockedHelper.isLocationEnabled(any())).thenReturn(isEnabled)
            return mockedHelper
        }

        fun prepareViewModel(
            locationPermissionHelper: LocationPermissionHelper,
            trackStateObserver: Observer<Boolean>? = null,
            grantObserver: Observer<Unit>? = null,
            enableObserver: Observer<Unit>? = null
        ): MainViewModel {

            val mockPreferences = prepareMockPreferences()
            val mockApplication: Application = mock()
            whenever(mockApplication.getSharedPreferences(any(), any())).thenReturn(mockPreferences)

            val viewModel = MainViewModel(mockApplication)
            viewModel.setTestLocationPermissionHelper(locationPermissionHelper)
            viewModel.setTestStartServiceAction { mockPreferences.optTracking() }
            viewModel.setTestStopServiceAction { mockPreferences.optNotTracking() }

            trackStateObserver?.let { viewModel.tracking.observeForever(it) }
            enableObserver?.let { viewModel.enable.observeForever(it) }
            grantObserver?.let { viewModel.grant.observeForever(it) }

            return viewModel
        }

        private fun prepareMockPreferences(): SharedPreferences {
            val mockPreferences: SharedPreferences = mock()
            val mockEditor: SharedPreferences.Editor = mock()

            whenever(mockPreferences.edit()).thenReturn(mockEditor)
            whenever(mockPreferences.registerOnSharedPreferenceChangeListener(any())).then { invocation ->
                listener = invocation.arguments[0] as SharedPreferences.OnSharedPreferenceChangeListener
                return@then null
            }
            whenever(mockPreferences.getBoolean(eq("tracking"), anyBoolean())).then { invocation ->
                return@then isTracking ?: invocation.arguments[1] as Boolean
            }
            whenever(mockEditor.putBoolean(eq("tracking"), anyBoolean())).then { invocation ->
                isTracking = invocation.arguments[1] as Boolean
                listener?.onSharedPreferenceChanged(mockPreferences, "tracking")
                return@then null
            }

            isTracking = null
            listener = null
            return mockPreferences
        }

    }
}
