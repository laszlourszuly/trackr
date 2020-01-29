package com.echsylon.komoot.screens.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.echsylon.komoot.screens.main.MainViewModelTestHelper.Companion.prepareLocationPermissionHelper
import com.echsylon.komoot.screens.main.MainViewModelTestHelper.Companion.prepareViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun when_starting_tracking__track_state_is_changed_to_true() {
        val mockHelper = prepareLocationPermissionHelper()
        val mockObserver: Observer<Boolean> = mock()
        val viewModel = prepareViewModel(mockHelper, trackStateObserver = mockObserver)
        viewModel.startTracking()

        val captor = ArgumentCaptor.forClass(Boolean::class.java)
        verify(mockObserver, times(2)).onChanged(captor.capture())  // +1 for initial state
        assertThat(captor.value).isEqualTo(true)
    }

    @Test
    fun when_stopping_tracking__track_state_is_changed_to_false() {
        val mockHelper = prepareLocationPermissionHelper()
        val mockObserver: Observer<Boolean> = mock()
        val viewModel = prepareViewModel(mockHelper, trackStateObserver = mockObserver)
        viewModel.startTracking()
        viewModel.stopTracking()

        val captor = ArgumentCaptor.forClass(Boolean::class.java)
        verify(mockObserver, times(3)).onChanged(captor.capture())  // +1 for initial state, +1 for start tracking
        assertThat(captor.value).isEqualTo(false)
    }

    @Test
    fun when_toggling_tracking__track_state_is_changed_to_true_and_then_false() {
        val mockHelper = prepareLocationPermissionHelper()
        val mockObserver: Observer<Boolean> = mock()
        val viewModel = prepareViewModel(mockHelper, trackStateObserver = mockObserver)
        viewModel.toggleTracking()
        viewModel.toggleTracking()

        val captor = ArgumentCaptor.forClass(Boolean::class.java)
        verify(mockObserver, times(3)).onChanged(captor.capture())  // +1 for initial state
        assertThat(captor.allValues[0]).isEqualTo(false)            // initial state
        assertThat(captor.allValues[1]).isEqualTo(true)
        assertThat(captor.allValues[2]).isEqualTo(false)
    }

    @Test
    fun when_starting_tracking_and_permission_missing__grant_event_is_fired() {
        val mockHelper = prepareLocationPermissionHelper(hasPermission = false)
        val mockObserver: Observer<Unit> = mock()
        val viewModel = prepareViewModel(mockHelper, grantObserver = mockObserver)
        viewModel.startTracking()
        verify(mockObserver, times(1)).onChanged(null)
    }

    @Test
    fun when_starting_tracking_and_locations_disabled__enable_event_is_fired() {
        val mockHelper = prepareLocationPermissionHelper(isEnabled = false)
        val mockObserver: Observer<Unit> = mock()
        val viewModel = prepareViewModel(mockHelper, enableObserver = mockObserver)
        viewModel.startTracking()
        verify(mockObserver, times(1)).onChanged(null)
    }

    @Test
    fun when_starting_tracking_and_permission_missing_and_locations_disabled__grant_event_is_fired() {
        val mockHelper = prepareLocationPermissionHelper(hasPermission = false, isEnabled = false)
        val mockGrantObserver: Observer<Unit> = mock()
        val mockEnableObserver: Observer<Unit> = mock()
        val viewModel = prepareViewModel(
            mockHelper,
            grantObserver = mockGrantObserver,
            enableObserver = mockEnableObserver
        )

        viewModel.startTracking()
        verify(mockGrantObserver, times(1)).onChanged(null)
        verify(mockEnableObserver, times(0)).onChanged(null)
    }
}
