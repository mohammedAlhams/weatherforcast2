package com.example.greenenergyapp.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greenenergyapp.AppDataBaseRepository
import com.example.greenenergyapp.Location
import com.example.greenenergyapp.LocationService
import com.example.greenenergyapp.NasaApiCallback
import com.example.greenenergyapp.NasaDataResponse
import com.example.greenenergyapp.ProfileActivity
import com.example.greenenergyapp.User
import com.example.greenenergyapp.WeatherData
import com.example.greenenergyapp.fetchNasaData
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Location screen that displays user's current location and NASA POWER API data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(user: User) {
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    val scope = rememberCoroutineScope()
    val db = remember { AppDataBaseRepository.getInstance(context) }

    // State variables
    var locationText by remember { mutableStateOf("Tap the button to get your location") }
    var isLocationLoading by remember { mutableStateOf(false) }
    var isNasaDataLoading by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var nasaData by remember { mutableStateOf<NasaDataResponse?>(null) }
    var nasaError by remember { mutableStateOf("") }
    var isLocationSaved by remember { mutableStateOf(false) }
    var savedLocationId by remember { mutableStateOf(-1) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Function to fetch NASA data - defined before it's called
    fun getNasaData(lat: Double, long: Double) {
        isNasaDataLoading = true
        nasaError = ""

        fetchNasaData(lat, long, object : NasaApiCallback {
            override fun onNasaDataSuccess(data: NasaDataResponse) {
                nasaData = data
                isNasaDataLoading = false
            }

            override fun onNasaDataError(message: String) {
                nasaError = message
                isNasaDataLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solar & Location Data") },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, ProfileActivity::class.java)
                        intent.putExtra("userId", user.userId)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Location icon
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Your Location",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Location information card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = locationText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (latitude != 0.0 && longitude != 0.0) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "Latitude: $latitude",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Longitude: $longitude",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Get location button
            Button(
                onClick = {
                    if (locationService.hasLocationPermissions()) {
                        isLocationLoading = true
                        locationText = "Fetching your location..."

                        locationService.getCurrentLocation(object : LocationService.LocationCallback {
                            override fun onLocationReceived(lat: Double, long: Double) {
                                latitude = lat
                                longitude = long
                                locationText = "Location found!"
                                isLocationLoading = false

                                // Automatically fetch NASA data once we have location
                                getNasaData(lat, long)
                            }

                            override fun onLocationFailed(errorMessage: String) {
                                locationText = "Could not get location"
                                isLocationLoading = false
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        // Request permissions through the activity
                        val activity = context as? ComponentActivity
                        activity?.let {
                            locationService.requestLocationPermissions(it)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLocationLoading && !isNasaDataLoading
            ) {
                if (isLocationLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Getting Location...")
                } else {
                    Text("Get Current Location")
                }
            }

            val isLocationFetched = latitude != 0.0 && longitude != 0.0

            if (isLocationFetched) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val existingLocation =
                                    db.getLocationDAO().getSingleLocationByUserId(user.userId)

                                if (existingLocation != null) {
                                    // Update existing location
                                    val updatedLocation = existingLocation.copy(
                                        latitude = latitude.toString(),
                                        longitude = longitude.toString()
                                    )
                                    db.getLocationDAO().updateLocation(updatedLocation)
                                    Toast.makeText(context, "Location updated!", Toast.LENGTH_SHORT)
                                        .show()
                                    isLocationSaved = true
                                    savedLocationId = existingLocation.locationId
                                } else {
                                    // Insert new location
                                    val newLocation = Location(
                                        latitude = latitude.toString(),
                                        longitude = longitude.toString(),
                                        availableArea = 0f, // set actual value if needed
                                        areaType = "Unknown",
                                        userId = user.userId
                                    )
                                    // Capture the returned ID from the insert operation
                                    val newLocationId = db.getLocationDAO().insertLocation(newLocation)
                                    savedLocationId = newLocationId.toInt()
                                    isLocationSaved = true
                                    Toast.makeText(context, "Location saved!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error saving location: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLocationLoading && !isNasaDataLoading
                ) {
                    Text("Save Location")
                }
            }


            // Add the "Save Weather Data" button after the location is saved
            if (isLocationSaved && nasaData != null && savedLocationId > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                // First check if weather data already exists for this location
                                val existingWeatherData = db.getWeatherDataDAO()
                                    .getSingleWeatherDataByLocationId(savedLocationId)

                                // Create a WeatherData object with the NASA data
                                val weatherData = WeatherData(
                                    // If updating existing data, keep its ID, otherwise auto-generate
                                    weatherDataId = existingWeatherData?.weatherDataId ?: 0,
                                    locationId = savedLocationId,
                                    solarIrradiance = if (nasaData?.solarIrradiance ?: -999.0 > -990) nasaData?.solarIrradiance ?: 0.0 else 0.0,
                                    windSpeed = if (nasaData?.windSpeed ?: -999.0 > -990) nasaData?.windSpeed ?: 0.0 else 0.0,
                                    peakSunHours = if (nasaData?.peakSunHours ?: -999.0 > -990) nasaData?.peakSunHours ?: 0.0 else 0.0,
                                    airDensity = if (nasaData?.airDensity ?: -999.0 > -990) nasaData?.airDensity ?: 0.0 else 0.0,
                                    optimalPanelAngle = if (nasaData?.optimalTiltAngle ?: -999.0 > -990) nasaData?.optimalTiltAngle ?: 0.0 else 0.0
                                )

                                if (existingWeatherData != null) {
                                    // Update existing weather data
                                    db.getWeatherDataDAO().updateWeatherData(weatherData)
                                    Toast.makeText(context, "Weather data updated!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Insert new weather data
                                    db.getWeatherDataDAO().insertWeatherData(weatherData)
                                    Toast.makeText(context, "Weather data saved!", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error saving weather data: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isNasaDataLoading
                ) {
                    Text("Save Weather Data")
                }
            }

            // NASA data section
            if (nasaData != null || isNasaDataLoading || nasaError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Solar Data",
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "NASA POWER Data",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when {
                            isNasaDataLoading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Fetching NASA POWER data...")
                            }
                            nasaError.isNotEmpty() -> {
                                Text(
                                    text = "Error: $nasaError",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (latitude != 0.0 && longitude != 0.0) {
                                            getNasaData(latitude, longitude)
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Try Again")
                                }
                            }
                            nasaData != null -> {
                                nasaData?.let { data ->
                                    // Display NASA data info
                                    Text(
                                        text = "Solar & Climate Data",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Format the date for display
                                    val displayDate = try {
                                        val apiDateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
                                        val displayDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
                                        val date = apiDateFormat.parse(data.date)
                                        displayDateFormat.format(date)
                                    } catch (e: Exception) {
                                        data.date
                                    }

                                    Text(
                                        text = "Date: $displayDate",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Data grid layout
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        // Solar Irradiance
                                        DataRow(
                                            label = "Solar Irradiance",
                                            value = if (data.solarIrradiance > -990) "${String.format("%.2f", data.solarIrradiance)} W/m²" else "No data available",
                                            description = "Available solar energy"
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Wind Speed
                                        DataRow(
                                            label = "Wind Speed",
                                            value = if (data.windSpeed > -990) "${String.format("%.2f", data.windSpeed)} m/s" else "No data available",
                                            description = "At 10m above surface"
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Air Density
                                        DataRow(
                                            label = "Air Density",
                                            value = if (data.airDensity > -990) "${String.format("%.3f", data.airDensity)} kg/m³" else "No data available",
                                            description = "Calculated from temperature and humidity"
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Temperature
                                        DataRow(
                                            label = "Temperature",
                                            value = if (data.temperature > -990) "${String.format("%.1f", data.temperature)}°C" else "No data available",
                                            description = "At 2m above surface"
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Relative Humidity
                                        DataRow(
                                            label = "Relative Humidity",
                                            value = if (data.humidity > -990) "${String.format("%.1f", data.humidity)}%" else "No data available",
                                            description = "At 2m above surface"
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Peak Sun Hours
                                        DataRow(
                                            label = "Peak Sun Hours",
                                            value = if (data.peakSunHours > -990) "${String.format("%.1f", data.peakSunHours)} hours" else "No data available",
                                            description = "Equivalent hours at 1000 W/m²"
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Optimal Panel Angle
                                        DataRow(
                                            label = "Optimal Panel Angle",
                                            value = if (data.optimalTiltAngle > -990) "${String.format("%.1f", data.optimalTiltAngle)}°" else "No data available",
                                            description = "Recommended tilt angle for fixed panels"
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Current request time
                                    val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy • h:mm a", Locale.getDefault())
                                    val currentDate = dateFormat.format(Date())

                                    Text(
                                        text = "Retrieved: $currentDate",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // If no NASA data yet, show a button to manually fetch it
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        if (latitude != 0.0 && longitude != 0.0) {
                            getNasaData(latitude, longitude)
                        } else {
                            Toast.makeText(
                                context,
                                "Please get your location first",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = latitude != 0.0 && longitude != 0.0 && !isLocationLoading && !isNasaDataLoading
                ) {
                    Text("Get NASA POWER Data")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information about the data
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "About This Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The data is provided by NASA's POWER (Prediction of Worldwide Energy Resources) API. Solar irradiance indicates the potential for solar energy generation at your location. Wind speed can help assess wind energy potential. Air density affects both solar and wind energy efficiency.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun DataRow(label: String, value: String, description: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    GreenEnergyAppTheme {
        LocationScreen(User(-1, "dummy@email.com", "1234", 25.36f, 25.36f))
    }
}