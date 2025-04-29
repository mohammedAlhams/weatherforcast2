package com.example.greenenergyapp.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greenenergyapp.AppDataBaseRepository
import com.example.greenenergyapp.ApplianceManagement
import com.example.greenenergyapp.User
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import com.example.greenenergyapp.ui.theme.GreenLight
import kotlinx.coroutines.launch

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAppliancesScreen() {
    GreenEnergyAppTheme {
        AppliancesScreen(User(-1, "dummy@email.com", "1234", 25.36f, 25.36f))
    }
}

@Composable
fun AppliancesScreen(user: User) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val applianceDAO = remember { AppDataBaseRepository.getInstance(context).getApplianceManagementDAO() }

    // State variables
    var monthlyBill by remember { mutableStateOf("") }
    var totalKwhPerMonthBox by remember { mutableStateOf("") }
    var totalKwhPerMonth by remember { mutableStateOf("0") }
    var costPerKwh by remember { mutableStateOf("") }


    // Add this LaunchedEffect to load data when the screen first appears
    LaunchedEffect(user.userId) {
        // Get the user's energy consumption and bill data from the database
        val currentUser = AppDataBaseRepository.getInstance(context).getUserDAO().getUserById(user.userId)
        currentUser?.let { dbUser ->
            // Set the total kWh value from database
            dbUser.monthlyEnergyConsumption?.let { consumption ->
                totalKwhPerMonth = consumption.toInt().toString()
            }

            // Set the monthly bill value from database
            dbUser.monthlyBillAmount?.let { billAmount ->
                monthlyBill = billAmount.toString()
            }

            // Calculate cost per kWh if both values are valid and non-zero
            if ((dbUser.monthlyEnergyConsumption ?: 0f) > 0f &&
                (dbUser.monthlyBillAmount ?: 0f) > 0f) {
                val calculatedCost = (dbUser.monthlyBillAmount ?: 0f) /
                        (dbUser.monthlyEnergyConsumption ?: 1f)
                costPerKwh = String.format("%.2f", calculatedCost)
            }
        }
    }

    // Appliance list state
    val applianceOptions = listOf("LED Bulb", "Laptop", "WiFi Router", "Microwave", "Washing Machine", "Kettle", "Hair Dryer", "Iron", "Desktop Computer", "Electric Stove", "Oven", "Vacuum Cleaner", "Game Console", "Air Conditioner", "Tumble Dryer", "Heater Fan", "Geyser / Water Heater", "Oil Heater", "Pool Pump")

    // List of appliances with their quantities
    val appliances = remember {
        mutableStateListOf<ApplianceItem>()
    }

    // Get appliances from database
    val appliancesLiveData = remember { applianceDAO.getAppliancesByUserId(user.userId) }
    val databaseAppliances by appliancesLiveData.observeAsState(emptyList())

    // When database appliances change, update our UI list
    LaunchedEffect(databaseAppliances) {
        if (databaseAppliances.isNotEmpty()) {
            // Clear existing appliances
            appliances.clear()

            // Add appliances from database
            databaseAppliances.forEach { dbAppliance ->
                appliances.add(
                    ApplianceItem(
                        id = dbAppliance.applianceId,
                        type = dbAppliance.applianceType,
                        quantity = dbAppliance.usageFrequency.toInt()
                    )
                )
            }
        } else if (appliances.isEmpty()) {
            // Add default items if no database items and our list is empty
            appliances.add(ApplianceItem(id = -1, type = "select", quantity = 1))
        }
    }

    // Function to save appliance to the database
    fun saveApplianceToDatabase(appliance: ApplianceItem) {
        // Skip if the user hasn't selected an appliance type
        if (appliance.type == "select") return

        scope.launch {
            try {
                // Check if this appliance already exists in the database
                val existingAppliance = if (appliance.id > 0) {
                    applianceDAO.getApplianceManagementById(appliance.id)
                } else null

                // Create the database entity from the UI model
                val applianceEntity = if (existingAppliance != null) {
                    // Update existing entity
                    existingAppliance.copy(
                        applianceType = appliance.type,
                        usageFrequency = appliance.quantity.toFloat()
                    )
                } else {
                    // Create new entity with ID = 0 to let Room auto-generate ID
                    ApplianceManagement(
                        applianceId = 0, // Let Room auto-generate ID
                        applianceType = appliance.type,
                        usageFrequency = appliance.quantity.toFloat(),
                        purchaseYear = 2023,
                        userId = user.userId
                    )
                }

                // Insert or update in database
                if (existingAppliance != null) {
                    applianceDAO.updateApplianceManagement(applianceEntity)
                } else {
                    applianceDAO.insertApplianceManagement(applianceEntity)
                }
            } catch (e: Exception) {
                Log.e("AppliancesScreen", "Error saving appliance: ${e.message}", e)
                Toast.makeText(context, "Error saving appliance", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to delete appliance from database
    fun deleteApplianceFromDatabase(applianceId: Int) {
        scope.launch {
            // Only attempt to delete if it's an existing database record (id > 0)
            if (applianceId > 0) {
                val applianceEntity = applianceDAO.getApplianceManagementById(applianceId)
                if (applianceEntity != null) {
                    applianceDAO.deleteApplianceManagement(applianceEntity)
                }
            }
        }
    }

    // Calculate total kWh based on the appliances
    val calculatedTotalKwh = calculateUsage(appliances)

    // First use the value from the input field if it's not empty, otherwise use the calculated value
    val displayedKwhPerMonth = if (totalKwhPerMonthBox.isNotBlank()) totalKwhPerMonthBox else calculatedTotalKwh.toString()

    // Calculate cost per kWh safely
    val kwhValue = displayedKwhPerMonth.toFloatOrNull() ?: 0f
    val displayedCostPerKwh = if (kwhValue > 0) kwhValue * 1.859217f else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 16.dp, 0.dp, 0.dp)
        ) {
            // Monthly Electricity Bill
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GreenLight.copy(alpha = 0.25f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Monthly Electricity Bill",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = totalKwhPerMonthBox,
                        onValueChange = { totalKwhPerMonthBox = it },
                        label = { Text("Enter your monthly bill (kWh)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Appliances Section
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GreenLight.copy(alpha = 0.25f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Appliances",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Button(
                            onClick = {
                                val newId = if (appliances.isEmpty()) -1 else
                                    appliances.minOfOrNull { it.id }?.let { it - 1 } ?: -1
                                appliances.add(ApplianceItem(id = newId, type = "select", quantity = 1))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Add Appliance",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        items(appliances) { appliance ->
                            ApplianceItemCard(
                                appliance = appliance,
                                options = applianceOptions,
                                onTypeChanged = { id, newType ->
                                    val index = appliances.indexOfFirst { it.id == id }
                                    if (index != -1) {
                                        appliances[index] = appliances[index].copy(type = newType)
                                    }
                                },
                                onQuantityChanged = { id, delta ->
                                    val index = appliances.indexOfFirst { it.id == id }
                                    if (index != -1) {
                                        val newQuantity = (appliances[index].quantity + delta).coerceAtLeast(1)
                                        appliances[index] = appliances[index].copy(quantity = newQuantity)
                                    }
                                },
                                onDelete = { id ->
                                    // Delete from database if it's a stored item
                                    deleteApplianceFromDatabase(id)
                                    // Remove from UI list
                                    appliances.removeAll { it.id == id }
                                }
                            )
                        }
                    }
                }
            }

            // Estimated Energy Usage
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GreenLight.copy(alpha = 0.25f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Estimated Energy Usage",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Total kWh per month
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Total kWh/month",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )

                            Text(
                                text = displayedKwhPerMonth,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Cost per kWh
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cost per kWh",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "TL" + "%.2f".format(displayedCostPerKwh),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            try {
                                // Get the kWh value safely
                                val totalKwh = displayedKwhPerMonth.toFloatOrNull() ?: calculatedTotalKwh.toFloat()

                                // Save all appliances to database
                                appliances.forEach { appliance ->
                                    if (appliance.type != "select") {
                                        saveApplianceToDatabase(appliance)
                                    }
                                }

                                // Update user's monthly energy consumption and bill amount
                                scope.launch {
                                    try {
                                        val updatedUser = user.copy(
                                            monthlyEnergyConsumption = totalKwh,
                                            monthlyBillAmount = displayedCostPerKwh  // Store the calculated cost
                                        )

                                        AppDataBaseRepository.getInstance(context).getUserDAO().updateUser(updatedUser)
                                        Toast.makeText(context, "Energy data saved", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Log.e("AppliancesScreen", "Error updating user: ${e.message}", e)
                                        Toast.makeText(context, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("AppliancesScreen", "Calculate & Save error: ${e.message}", e)
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Calculate & Save",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Bottom Navigation Bar
            BottomNavigationBar(user)
        }
    }
}

@Composable
fun ApplianceItemCard(
    appliance: ApplianceItem,
    options: List<String>,
    onTypeChanged: (Int, String) -> Unit,
    onQuantityChanged: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                // Enhanced dropdown field with better visual cues
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (appliance.type == "select") "Select an appliance" else appliance.type,
                            color = if (appliance.type == "select")
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand dropdown",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // The dropdown menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) {
                            (LocalConfiguration.current.screenWidthDp.dp * 0.68f)
                        })
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onTypeChanged(appliance.id, option)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onQuantityChanged(appliance.id, -1) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = appliance.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                IconButton(
                    onClick = { onQuantityChanged(appliance.id, 1) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { onDelete(appliance.id) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// Enhanced data class for appliance items with energy information
data class ApplianceItem(
    val id: Int,
    val type: String,
    val quantity: Int
)

// Enhanced energy usage calculator
fun calculateUsage(appliances: List<ApplianceItem>): Int {
    // Energy consumption data for each appliance type in kWh per month per unit
    val applianceEnergyData = mapOf(
        "LED Bulb" to 9,                // 0.01 kWh × 3h/day × 30
        "Laptop" to 15,                 // 0.05 kWh × 10h/day × 30
        "WiFi Router" to 11,            // 0.015 kWh × 24h/day × 30
        "Microwave" to 18,              // 0.6 kWh × 1h/day × 30
        "Washing Machine" to 12,        // 0.6 kWh × 0.67h/day × 30 (20 loads/mo)
        "Kettle" to 18,                 // 1.2 kWh × 0.5h/day × 30
        "Hair Dryer" to 18,             // 1.2 kWh × 0.5h/day × 30
        "Iron" to 24,                   // 1.2 kWh × 0.67h/day × 30
        "Desktop Computer" to 90,       // 1.5 kWh × 2h/day × 30
        "Electric Stove" to 90,         // 3.0 kWh × 1h/day × 30
        "Oven" to 45,                   // 3.0 kWh × 0.5h/day × 30
        "Vacuum Cleaner" to 9,          // 0.8 kWh × 0.375h/day × 30 (11.25h/mo)
        "Game Console" to 18,           // 1.2 kWh × 0.5h/day × 30
        "Air Conditioner" to 180,       // 3.0 kWh × 2h/day × 30
        "Tumble Dryer" to 36,           // 1.8 kWh × 0.67h/day × 30 (20 loads/mo)
        "Heater Fan" to 90,             // 1.8 kWh × 1.67h/day × 30 (seasonal)
        "Geyser / Water Heater" to 105, // 3.5 kWh × 1h/day × 30
        "Oil Heater" to 150,            // 2.5 kWh × 2h/day × 30
        "Pool Pump" to 180              // 2.5 kWh × 2.4h/day × 30
    )

    // Calculate total kWh based on appliance types and quantities
    var totalKwh = 0

    appliances.forEach { appliance ->
        // Only calculate for valid selections (not "select")
        if (appliance.type != "select" && applianceEnergyData.containsKey(appliance.type)) {
            // Multiply energy usage by quantity
            val applianceKwh = applianceEnergyData[appliance.type] ?: 0
            totalKwh += applianceKwh * appliance.quantity
        }
    }

    return totalKwh
}

// Function to estimate cost per kWh from monthly bill
fun estimateCostPerKwh(monthlyBill: String, totalKwh: Int): Float {
    if (monthlyBill.isBlank() || totalKwh <= 0) {
        return 0f
    }

    val billAmount = monthlyBill.toFloatOrNull() ?: return 0f
    return billAmount / totalKwh
}