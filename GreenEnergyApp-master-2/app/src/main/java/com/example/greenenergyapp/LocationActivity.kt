package com.example.greenenergyapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.greenenergyapp.screens.LocationScreen
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme

/**
 * Activity that hosts the LocationScreen composable.
 * Handles permission results and provides navigation capabilities.
 */
class LocationActivity : ComponentActivity() {

    // The location service used by the composable
    private lateinit var locationService: LocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the location service
        locationService = LocationService(this)

        val userId = intent.getIntExtra("userId", 0)
        setContent {
            var user by remember { mutableStateOf<User?>(null) }
            val context = LocalContext.current
            LaunchedEffect(userId) {
                user = AppDataBaseRepository.getInstance(context).getUserDAO().getUserById(userId)
            }
            GreenEnergyAppTheme {
                if (user != null) {
                    LocationScreen(user!!)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }


    /**
     * Handle permission results from the system permission dialog
     */
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LocationService.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted - the LocationScreen will handle location retrieval
                // No need to do anything here as the composable will detect permission changes
            }
        }
    }

    /**
     * Handle back button press
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}