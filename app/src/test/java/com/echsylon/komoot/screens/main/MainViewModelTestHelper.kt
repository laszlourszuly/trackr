package com.echsylon.komoot.screens.main

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import com.echsylon.komoot.location.LocationPermissionHelper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

class MainViewModelTestHelper {

    companion object {

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
            val mockSharedPreference: SharedPreferences = mock()
            val mockApplication: Application = mock()
            whenever(mockApplication.getSharedPreferences(any(), any())).thenReturn(mockSharedPreference)
            val viewModel = MainViewModel(mockApplication)
            locationPermissionHelper.let { viewModel.setLocationPermissionHelper(it) }
            trackStateObserver?.let { viewModel.tracking.observeForever(it) }
            enableObserver?.let { viewModel.enable.observeForever(it) }
            grantObserver?.let { viewModel.grant.observeForever(it) }
            return viewModel
        }

    }
}