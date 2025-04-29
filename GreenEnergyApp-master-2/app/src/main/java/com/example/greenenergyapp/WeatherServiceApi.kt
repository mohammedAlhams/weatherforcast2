package com.example.greenenergyapp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// API key for OpenWeatherMap
const val WEATHER_API_KEY = "ffda0467c7677e2b32539aaa34c594ae"
private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

/**
 * Interface for OpenWeatherMap API endpoints
 */
interface WeatherApiService {
    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = WEATHER_API_KEY
    ): Call<WeatherResponse>
}

/**
 * Singleton object to access the weather API
 */
object WeatherApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}

/**
 * Weather API callback interface
 */
interface WeatherCallback {
    fun onWeatherSuccess(weather: WeatherResponse)
    fun onWeatherError(message: String)
}

/**
 * Helper function to fetch weather data
 */
fun fetchWeather(latitude: Double, longitude: Double, callback: WeatherCallback) {
    WeatherApi.service.getCurrentWeather(latitude, longitude).enqueue(object : Callback<WeatherResponse> {
        override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
            if (response.isSuccessful) {
                response.body()?.let { weather ->
                    callback.onWeatherSuccess(weather)
                } ?: callback.onWeatherError("Empty response body")
            } else {
                callback.onWeatherError("Error: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            callback.onWeatherError("Network error: ${t.message}")
        }
    })
}

/**
 * Data classes for OpenWeatherMap API response
 */
data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val name: String
)

data class Coord(val lon: Double, val lat: Double)
data class Weather(val id: Int, val main: String, val description: String, val icon: String)
data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val pressure: Int, val humidity: Int)
data class Wind(val speed: Double, val deg: Int)