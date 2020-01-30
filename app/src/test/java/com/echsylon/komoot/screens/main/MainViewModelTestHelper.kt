package com.echsylon.komoot.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.echsylon.komoot.TrackrApplication
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

class MainViewModelTestHelper {

    companion object {
        private val tracking = MutableLiveData<Boolean>(false)

        fun prepareViewModel(
            hasLocationsPermission: Boolean = true,
            hasLocationsEnabled: Boolean = true,
            trackingObserver: Observer<Boolean>? = null,
            grantObserver: Observer<Unit>? = null,
            enableObserver: Observer<Unit>? = null
        ): MainViewModel {
            tracking.postValue(false)

            val mockApplication: TrackrApplication = mock()
            whenever(mockApplication.tracking).thenReturn(tracking)
            whenever(mockApplication.startLocationService()).then { tracking.postValue(!tracking.value!!) }
            whenever(mockApplication.stopLocationService()).then { tracking.postValue(!tracking.value!!) }
            whenever(mockApplication.hasLocationPermissions()).thenReturn(hasLocationsPermission)
            whenever(mockApplication.isLocationEnabled()).thenReturn(hasLocationsEnabled)

            val viewModel = MainViewModel(mockApplication)

            trackingObserver?.let { viewModel.tracking.observeForever(it) }
            enableObserver?.let { viewModel.enable.observeForever(it) }
            grantObserver?.let { viewModel.grant.observeForever(it) }

            return viewModel
        }

    }
}
