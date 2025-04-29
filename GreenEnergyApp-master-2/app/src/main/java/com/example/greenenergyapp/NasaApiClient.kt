package com.example.greenenergyapp

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import kotlin.math.sin


/**
 * Data model for NASA POWER API response
 */
data class NasaDataResponse(
    val date: String,
    val solarIrradiance: Double, // W/m²
    val windSpeed: Double,       // m/s
    val temperature: Double,     // °C
    val humidity: Double,        // %
    val airDensity: Double,      // kg/m³
    val peakSunHours: Double,    // hours
    val optimalTiltAngle: Double // degrees
)

/**
 * Callback interface for NASA API calls
 */
interface NasaApiCallback {
    fun onNasaDataSuccess(data: NasaDataResponse)
    fun onNasaDataError(message: String)
}

/**
 * NASA POWER API Client
 */
class NasaApiClient {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "https://power.larc.nasa.gov/api/temporal/daily/point"

    /**
     * Fetch meteorological data from NASA POWER API
     */
    suspend fun fetchMeteorologicalData(
        latitude: Double,
        longitude: Double,
        callback: NasaApiCallback
    ) {
        try {
            val startDate = LocalDate.now().minusDays(30).format(DateTimeFormatter.BASIC_ISO_DATE)
            val endDate = LocalDate.now().minusDays(10).format(DateTimeFormatter.BASIC_ISO_DATE)

            val parameters = listOf(
                "ALLSKY_SFC_SW_DWN", // Solar irradiance
                "WS10M",             // Wind speed at 10m
                "RH2M",              // Relative humidity at 2m
                "T2M"                // Temperature at 2m
            )

            val url = "$baseUrl?" +
                    "parameters=${parameters.joinToString(",")}&" +
                    "community=RE&" +
                    "longitude=$longitude&" +
                    "latitude=$latitude&" +
                    "start=$startDate&" +
                    "end=$endDate&" +
                    "format=JSON"

            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = gson.fromJson(responseBody, JsonObject::class.java)
                    val nasaData = parseNasaResponse(jsonResponse, latitude) // Pass latitude here
                    withContext(Dispatchers.Main) {
                        callback.onNasaDataSuccess(nasaData)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback.onNasaDataError("Failed to fetch data: ${response.code}")
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callback.onNasaDataError("Error: ${e.message}")
            }
        }
    }

    /**
     * Parse NASA POWER API response
     */
    private fun parseNasaResponse(response: JsonObject, latitude: Double): NasaDataResponse {
        // Parse the response and extract the data
        val parameters = response.getAsJsonObject("properties").getAsJsonObject("parameter")

        // Get the latest day's data
        val latestDate = parameters.getAsJsonObject("ALLSKY_SFC_SW_DWN").entrySet().lastOrNull()?.key
            ?: throw Exception("No data available")

        val solarIrradiance = parameters.getAsJsonObject("ALLSKY_SFC_SW_DWN").get(latestDate).asDouble
        val windSpeed = parameters.getAsJsonObject("WS10M").get(latestDate).asDouble
        val temperature = parameters.getAsJsonObject("T2M").get(latestDate).asDouble
        val humidity = parameters.getAsJsonObject("RH2M").get(latestDate).asDouble

        // Calculate air density
        val airDensity = if (temperature > -990 && humidity > -990) {
            calculateAirDensity(temperature, humidity)
        } else {
            -999.0
        }

        // Calculate peak sun hours from solar irradiance
        val peakSunHours = if (solarIrradiance > -990) {
            calculatePeakSunHours(solarIrradiance)
        } else {
            -999.0
        }

        // Calculate optimal tilt angle
        val optimalTiltAngle = calculateOptimalTiltAngle(latestDate, latitude)

        return NasaDataResponse(
            date = latestDate,
            solarIrradiance = solarIrradiance,
            windSpeed = windSpeed,
            temperature = temperature,
            humidity = humidity,
            airDensity = airDensity,
            peakSunHours = peakSunHours,
            optimalTiltAngle = optimalTiltAngle
        )
    }

    /**
     * Calculate air density from temperature and humidity
     */
    private fun calculateAirDensity(temperatureC: Double, relativeHumidity: Double, pressureHPa: Double = 1013.25): Double {
        // Air density calculation (simplified)
        val temperatureK = temperatureC + 273.15
        return pressureHPa * 100 / (287.05 * temperatureK)
    }
    /**
     * Calculate peak sun hours from solar irradiance
     * Peak sun hours are defined as the equivalent number of hours per day when solar irradiance
     * equals 1000 W/m²
     */
    private fun calculatePeakSunHours(solarIrradiance: Double): Double {
        // Daily solar irradiance (kWh/m²/day) is roughly equivalent to peak sun hours
        // The ALLSKY_SFC_SW_DWN parameter from NASA POWER is in MJ/m²/day
        // 1 MJ/m² = 0.2778 kWh/m²
        return if (solarIrradiance > 0) {
            solarIrradiance * 0.2778
        } else {
            0.0
        }
    }

    /**
     * Calculate optimal solar panel tilt angle based on latitude and date
     */
    private fun calculateOptimalTiltAngle(dateString: String, latitude: Double): Double {
        // If no valid latitude, return error value
        if (latitude < -90 || latitude > 90) return -999.0

        // Parse date
        try {
            val year = dateString.substring(0, 4).toInt()
            val month = dateString.substring(4, 6).toInt()
            val day = dateString.substring(6, 8).toInt()

            // Calculate day of year
            val date = LocalDate.of(year, month, day)
            val dayOfYear = date.dayOfYear

            // Calculate declination angle
            val declination = 23.45 * sin(Math.toRadians(360.0/365 * (dayOfYear - 81)))

            // Calculate optimal tilt angle
            // Formula: latitude ± 15° (+ in winter, - in summer)
            // More accurate: latitude - declination
            val optimalAngle = latitude - declination

            // Ensure angle is within reasonable bounds
            return when {
                optimalAngle < 0 -> 0.0  // Don't recommend negative tilts
                optimalAngle > 90 -> 90.0 // Don't exceed 90 degrees
                else -> optimalAngle
            }
        } catch (e: Exception) {
            return -999.0
        }
    }
}

/**
 * Helper function to fetch NASA data (can be called from anywhere)
 */
fun fetchNasaData(latitude: Double, longitude: Double, callback: NasaApiCallback) {
    kotlinx.coroutines.GlobalScope.launch {
        NasaApiClient().fetchMeteorologicalData(latitude, longitude, callback)
    }
}