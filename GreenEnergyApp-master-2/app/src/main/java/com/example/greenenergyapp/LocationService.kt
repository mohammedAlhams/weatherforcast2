package com.example.greenenergyapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

/**
 * A service class that handles location-related functionality for the GreenEnergyApp.
 * It manages permissions and provides location coordinates.
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    /**
     * Interface for receiving location updates
     */
    interface LocationCallback {
        fun onLocationReceived(latitude: Double, longitude: Double)
        fun onLocationFailed(errorMessage: String)
    }

    /**
     * Checks if the app has the necessary location permissions
     * @return Boolean indicating if permissions are granted
     */
    fun hasLocationPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Requests the necessary location permissions
     * @param activity The activity requesting permissions
     */
    fun requestLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Gets the current device location
     * @param callback The callback to receive location updates
     */
    fun getCurrentLocation(callback: LocationCallback) {
        if (!hasLocationPermissions()) {
            callback.onLocationFailed("Location permissions not granted")
            return
        }

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(listener: OnTokenCanceledListener) =
                        CancellationTokenSource().token
                    override fun isCancellationRequested() = false
                }
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    callback.onLocationReceived(location.latitude, location.longitude)
                } else {
                    callback.onLocationFailed("Could not get location. Please try again.")
                }
            }.addOnFailureListener { e ->
                callback.onLocationFailed("Location error: ${e.message}")
            }
        } catch (e: SecurityException) {
            callback.onLocationFailed("Location permission denied")
        }
    }

    /**
     * Gets the last known location (faster but might be outdated)
     * @param callback The callback to receive location updates
     */
    fun getLastKnownLocation(callback: LocationCallback) {
        if (!hasLocationPermissions()) {
            callback.onLocationFailed("Location permissions not granted")
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    callback.onLocationReceived(location.latitude, location.longitude)
                } else {
                    // Fall back to current location if last known is null
                    getCurrentLocation(callback)
                }
            }.addOnFailureListener { e ->
                callback.onLocationFailed("Location error: ${e.message}")
            }
        } catch (e: SecurityException) {
            callback.onLocationFailed("Location permission denied")
        }
    }
}