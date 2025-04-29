package com.example.greenenergyapp.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greenenergyapp.HelpSupportActivity
import com.example.greenenergyapp.LocationActivity
import com.example.greenenergyapp.MainActivity
import com.example.greenenergyapp.R
import com.example.greenenergyapp.User
import com.example.greenenergyapp.UserFormActivity
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import com.example.greenenergyapp.ui.theme.GreenLight

@Preview
@Composable
fun ProfileScreenPreview() {
    GreenEnergyAppTheme {
        ProfileScreen(User(-1, "dummy@email.com", "1234", 25.36f, 25.36f))
    }

}


@Composable
fun ProfileScreen(user: User) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val activity = context as? Activity

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content with scroll
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Profile header text
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Profile Card
                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 24.dp),
//                    shape = RoundedCornerShape(16.dp),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = GreenLight.copy(alpha = 0.08f)
//                    )
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = GreenLight.copy(alpha = 0.30f)
                    )


                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Name
                        Text(
                            text = "Alex Johnson",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 12.dp)
                        )

                        // Email
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Edit Profile Button
                        Button(
                            onClick = { /* Handle edit profile */ },
                            modifier = Modifier
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = "Edit Profile",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Settings Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // User Form
                        ListItem(
                            headlineContent = {
                                Text(
                                    "User form",
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            supportingContent = {
                                Text("Update your details")
                            },
                            modifier = Modifier.clickable {
                                val intent = Intent(context, UserFormActivity::class.java)
                                intent.putExtra("userId", user.userId)
                                context.startActivity(intent)
                            }
                        )
                        Divider()

                        // Privacy & Security
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Privacy & Security",
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            supportingContent = {
                                Text("Manage your privacy settings")
                            }
                        )
                        Divider()

                        // Location
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Location",
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            supportingContent = {
                                Text("Update your location preferences")
                            }, modifier = Modifier.clickable {
                                val intent = Intent(context, LocationActivity::class.java)
                                intent.putExtra("userId", user.userId)
                                context.startActivity(intent)
                            }
                        )
                        Divider()
                    }
                }

                // Help & Support Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Help & Support",
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            supportingContent = {
                                Text("Get assistance and support")
                            },
                            modifier = Modifier.clickable {
                                val intent = Intent(context, HelpSupportActivity::class.java)
                                intent.putExtra("userId", user.userId)
                                context.startActivity(intent)
                            }
                        )
                        Divider()
                    }
                }

                // Log Out Button
                Button(
                    onClick = {
                        // Clear SharedPreferences
                        val sharedPref =
                            context.getSharedPreferences("GreenEnergyPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            remove("userId")
                            apply()
                        }

                        // Then navigate to the Login screen:
                        activity?.startActivity(Intent(context, MainActivity::class.java))
                        activity?.finish() // closes the current Activity if desired
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 44.dp)
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),

                    ) {
                    Text(
                        text = "Log Out",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            // Bottom Navigation Bar
            BottomNavigationBar(user)
        }
    }
}

