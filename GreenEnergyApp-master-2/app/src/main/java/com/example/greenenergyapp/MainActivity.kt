package com.example.greenenergyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.greenenergyapp.screens.LoginScreen
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if the user is already logged in
        val sharedPref = getSharedPreferences("GreenEnergyPrefs", MODE_PRIVATE)
        var savedUserId = sharedPref.getInt("userId", -1)


        if (savedUserId != -1) {
            // User is already logged in; navigate to HomeActivity
            startActivity(Intent(this, HomeActivity::class.java).apply {
                putExtra("userId", savedUserId)
            })
            finish() // Prevent going back to the login screen
        } else {
            // Show the login screen
            setContent {
                GreenEnergyAppTheme {
                    LoginScreen(context = this)
                }
            }
        }
    }


    private fun initializeDatabase() {
        val dbRepository = AppDataBaseRepository.getInstance(applicationContext)

        // Force database initialization by accessing all DAOs
        lifecycleScope.launch {
            val userDao = AppDataBaseRepository.getInstance(applicationContext).getUserDAO()

            // Insert or query to force table creation
            val newUserId = userDao.insertUser(
                User(
                    email = "test@test.com", password = "123", monthlyEnergyConsumption = 100f,
                    userId = TODO(),
                    monthlyBillAmount = TODO(),
                )
            )

            // Or do a simple query:
            val allUsers = userDao.getAllUsers().value  // If using a LiveData, might be null if empty
            // Alternatively, do a suspend query to get the data right away:
            val testUser = userDao.getUserById(newUserId.toInt())

            // Logging to confirm
            Log.d("DBCheck", "testUser = $testUser")
        }

    }
}