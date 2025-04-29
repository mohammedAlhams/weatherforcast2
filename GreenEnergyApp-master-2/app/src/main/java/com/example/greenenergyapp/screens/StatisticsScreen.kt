package com.example.greenenergyapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greenenergyapp.User
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import com.example.greenenergyapp.ui.theme.GreenLight

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatisticsScreenPreview() {
    GreenEnergyAppTheme {
        StatisticsScreen(User(-1, "dummy@email.com", "1234", 25.36f, 25.36f))
    }

}



@Composable
fun StatisticsScreen(user:User) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with gradient background
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 37.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(20.dp)
            ) {
                // Header row with title and refresh button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Energy Stats",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { /* Handle refresh */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Monthly consumption chart container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(16.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Monthly Consumption",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "kWh",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Placeholder for chart
                        Spacer(modifier = Modifier.height(200.dp))
                    }
                }
            }

            // Key metrics section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Key Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // First row of metric cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Energy metric card
                    MetricCard(
                        icon = Icons.Default.Bolt,
                        iconTint = Color.Green,
                        label = "Energy",
                        value = "487",
                        description = "kWh this month",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Appliances metric card
                    MetricCard(
                        icon = Icons.Default.Devices,
                        iconTint = Color(0xFFFFA000), // Warning color
                        label = "Appliances",
                        value = "12",
                        description = "devices connected",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Second row of metric cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Savings metric card
                    MetricCard(
                        icon = Icons.Default.TrendingDown,
                        iconTint = Color.Red,
                        label = "Savings",
                        value = "15%",
                        description = "vs. last month",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Green solutions metric card
                    MetricCard(
                        icon = Icons.Default.Eco,
                        iconTint = Color(0xFF009688), // Teal
                        label = "Green",
                        value = "3",
                        description = "solutions applied",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Bottom navigation bar
            BottomNavigationBar(user)
        }
    }
}

@Composable
fun MetricCard(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            ,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenLight.copy(alpha = 0.20f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}