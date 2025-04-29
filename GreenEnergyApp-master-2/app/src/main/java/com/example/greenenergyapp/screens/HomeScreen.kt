package com.example.greenenergyapp.screens


import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greenenergyapp.AppliancesActivity
import com.example.greenenergyapp.HomeActivity
import com.example.greenenergyapp.ProfileActivity
import com.example.greenenergyapp.StatusActivity
import com.example.greenenergyapp.User
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme

@Composable
fun HomeScreen(user: User) {
    Scaffold(
        topBar = { HeaderSection(user) },
        bottomBar = { BottomNavigationBar(user) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            QuickAccessGrid()
            Spacer(modifier = Modifier.height(16.dp))
            RecentActivity()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(user: User) {
    TopAppBar(
        title = {
            Column {
                Text("Device Manager", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Welcome back, ${user.email}", fontSize = 14.sp, color = Color.Gray)
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "User")
            }
        }
    )
}

@Composable
fun QuickAccessGrid() {

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Quick Access", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickAccessItem(Icons.Default.Devices, "Devices")
            QuickAccessItem(Icons.Default.Info, "General Info")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickAccessItem(Icons.Default.Build, "Maintenance")
            QuickAccessItem(Icons.Default.Lightbulb, "Solutions")
        }
    }
}


@Composable
fun QuickAccessItem(icon: ImageVector, title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)))
                )
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RecentActivity() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        RecentActivityItem(Icons.Default.Air, "AC Filter Changed", "Today, 10:30 AM")
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        RecentActivityItem(Icons.Default.Kitchen, "Refrigerator Maintenance", "Yesterday, 2:15 PM")
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun RecentActivityItem(icon: ImageVector, title: String, time: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.shadow(0.dp, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFBB86FC)).background(
                brush = Brush.verticalGradient(listOf(
                    Color(0xFF81C784),
                    Color(0xFF388E3C)
                ))
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(time, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun BottomNavigationBar(user: User) {
    val context = LocalContext.current
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
//            BottomNavigationItem(Icons.Default.Home, "Home")
//            BottomNavigationItem(Icons.Default.Devices, "Appliances")
//            BottomNavigationItem(Icons.Default.Assessment, "Status")
//            BottomNavigationItem(Icons.Default.Person, "Profile")

            BottomNavigationItem(Icons.Default.Home, "Home") {
                val intent = Intent(context, HomeActivity::class.java).apply {
                    putExtra("userId", user.userId)
                }
                context.startActivity(intent)
            }
            BottomNavigationItem(Icons.Default.Devices, "Appliances") {
                val intent = Intent(context, AppliancesActivity::class.java).apply {
                    putExtra("userId", user.userId)
                }
                context.startActivity(intent)
            }
            BottomNavigationItem(Icons.Default.Assessment, "Status") {
                val intent = Intent(context, StatusActivity::class.java).apply {
                    putExtra("userId", user.userId)
                }
                context.startActivity(intent)
            }
            BottomNavigationItem(Icons.Default.Person, "Profile") {
                val intent = Intent(context, ProfileActivity::class.java).apply {
                    putExtra("userId", user.userId)
                }
                context.startActivity(intent)
            }


        }


    }
}

@Composable
fun BottomNavigationItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onClick)
        )
        Text(title, fontSize = 12.sp)
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    GreenEnergyAppTheme {
        HomeScreen(User(-1, "dummy@email.com", "1234", 25.36f, 25.36f))
    }
}