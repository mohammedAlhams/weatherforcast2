package com.example.greenenergyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.greenenergyapp.screens.HelpSupportScreen
import com.example.greenenergyapp.screens.HomeScreen
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme

class HelpSupportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userId = intent.getIntExtra("userId", 0)
        setContent {
            var user by remember { mutableStateOf<User?>(null) }
            val context = LocalContext.current
            LaunchedEffect(userId) {
                user = AppDataBaseRepository.getInstance(context).getUserDAO().getUserById(userId)
            }
            GreenEnergyAppTheme {
                if (user != null) {
                    HelpSupportScreen(user!!)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}


