package com.example.greenenergyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import com.example.yourapp.ForgotPasswordScreen

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GreenEnergyAppTheme {
                ForgotPasswordScreen()
            }
        }
    }
}



