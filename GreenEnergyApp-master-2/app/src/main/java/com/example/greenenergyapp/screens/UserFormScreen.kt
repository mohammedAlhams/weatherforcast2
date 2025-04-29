package com.example.greenenergyapp.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.greenenergyapp.ProfileActivity
import com.example.greenenergyapp.User
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserFormScreenPreview() {
    GreenEnergyAppTheme {
        UserFormScreen(User(-1, "dummy@email.com", "1234", 25.36f, 25.36f))
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(user: User) {
    val scrollState = rememberScrollState()

    // State for form
    var energySource by remember { mutableStateOf("") }
    var kwhUsage by remember { mutableStateOf("") }
    var emptyQuestion by remember { mutableStateOf("") }
    var knowledgeRating by remember { mutableStateOf(0f) }
    var budget by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var preferredEnergySource by remember { mutableStateOf("") }
    var squareMeters by remember { mutableStateOf("") }
    var textAnswer by remember { mutableStateOf("") }
    var cubicMeters by remember { mutableStateOf("") }

    // Checkboxes for reasons
    var sustainabilitySelected by remember { mutableStateOf(false) }
    var savingMoneySelected by remember { mutableStateOf(false) }
    var independenceSelected by remember { mutableStateOf(false) }
    var globalWarmingSelected by remember { mutableStateOf(false) }
    var tryNewSelected by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Form",color=Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, ProfileActivity::class.java)
                        intent.putExtra("userId", user.userId)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question 1: Energy Source
            FormCard(title = "1 What is the available area in cubic meters?") {

                OutlinedTextField(
                    value = cubicMeters,
                    onValueChange = { newValue ->
                        // Filter input: only allow digits and optional dot
                        if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            cubicMeters = newValue
                        }
                    },
                    label = { Text("Enter area (m³)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Question 2: KWh Usage
            FormCard(title = "2 What is the type of the available area? ") {
                ChipGroup(
                    options = listOf("Rooftop", "Backyard", "Garage"),
                    selectedOption = kwhUsage,
                    onOptionSelected = { kwhUsage = it }
                )
            }

            // Question 3: Empty question
            FormCard(title = "3 Empty question") {
                ChipGroup(
                    options = listOf(
                        "Analytical and methodical",
                        "Creative and intuitive",
                        "Collaborative and discussion-based",
                        "Practical and experience-based"
                    ),
                    selectedOption = emptyQuestion,
                    onOptionSelected = { emptyQuestion = it }
                )
            }

            // Question 4: Knowledge Rating
            FormCard(title = "4 Rate your knowledge of renewable energy sources") {
                RatingBar(
                    rating = knowledgeRating,
                    onRatingChanged = { knowledgeRating = it }
                )
            }

            // Question 5: Budget
            FormCard(title = "5 How much are you willing to spend on renewable energy source?") {
                ChipGroup(
                    options = listOf(
                        "100-500USD",
                        "500-1000USD",
                        "1000-1500USD",
                        "1500-2000USD",
                        "2000USD+"
                    ),
                    selectedOption = budget,
                    onOptionSelected = { budget = it }
                )
            }

            // Question 6: Address
            FormCard(title = "6 Write down your address") {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("Country City street etc..") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            }

            // Question 7: Reasons
            FormCard(title = "7 Reason for selecting renewable energy source") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckboxRow("Sustainability", sustainabilitySelected) {
                        sustainabilitySelected = it
                    }
                    CheckboxRow("Saving money", savingMoneySelected) { savingMoneySelected = it }
                    CheckboxRow(
                        "Independence from grid",
                        independenceSelected
                    ) { independenceSelected = it }
                    CheckboxRow("Global warming", globalWarmingSelected) {
                        globalWarmingSelected = it
                    }
                    CheckboxRow(
                        "Want to try something different",
                        tryNewSelected
                    ) { tryNewSelected = it }
                }
            }

            // Question 8: Square meters
            FormCard(title = "8 How many sqm can you spare?") {
                ChipGroup(
                    options = listOf("0-5sqm", "5-10sqm", "10-15sqm", "15-20sqm", "20-25sqm"),
                    selectedOption = squareMeters,
                    onOptionSelected = { squareMeters = it }
                )
            }

            // Question 9: Preferred energy source
            FormCard(title = "9 Preferred energy source?") {
                ChipGroup(
                    options = listOf("Solar", "Wind", "Hybrid"),
                    selectedOption = preferredEnergySource,
                    onOptionSelected = { preferredEnergySource = it }
                )
            }

            // Question 10: Empty question with text field
            FormCard(title = "10 empty question") {
                OutlinedTextField(
                    value = textAnswer,
                    onValueChange = { textAnswer = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Type your answer here...") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            }

            // Submit button
            Button(
                onClick = { /* Handle form submission */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Text("Submit Answers", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FormCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
fun ChipGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    FlowRow(
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(
                        value = isSelected,
                        onValueChange = { if (it) onOptionSelected(option) },
                        role = Role.RadioButton
                    ),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                        0.12f
                    )
                ),
                tonalElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(
                        0.6f
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FlowRow(
    mainAxisSpacing: Dp = 0.dp,
    crossAxisSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content
    ) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Measure and place children
        for (measurable in measurables) {
            // Measure child
            val placeable = measurable.measure(constraints)

            // If current sequence fits with new child, add it
            if (currentMainAxisSize + placeable.width + (if (currentSequence.isEmpty()) 0 else mainAxisSpacing.roundToPx()) <= constraints.maxWidth) {
                currentSequence.add(placeable)
                currentMainAxisSize += placeable.width + (if (currentSequence.size > 1) mainAxisSpacing.roundToPx() else 0)
                currentCrossAxisSize = maxOf(currentCrossAxisSize, placeable.height)
            } else {
                // Current sequence is full, start a new one
                sequences.add(currentSequence.toList())
                crossAxisSizes.add(currentCrossAxisSize)
                crossAxisSpace += if (sequences.size > 1) crossAxisSpacing.roundToPx() else 0

                currentSequence.clear()
                currentSequence.add(placeable)
                currentMainAxisSize = placeable.width
                currentCrossAxisSize = placeable.height
            }
        }

        // Add last sequence
        if (currentSequence.isNotEmpty()) {
            sequences.add(currentSequence.toList())
            crossAxisSizes.add(currentCrossAxisSize)
        }

        // Calculate cross axis positions
        var crossAxisPosition = 0
        for (i in crossAxisSizes.indices) {
            crossAxisPositions.add(crossAxisPosition)
            crossAxisPosition += crossAxisSizes[i] + (if (i < crossAxisSizes.size - 1) crossAxisSpacing.roundToPx() else 0)
        }

        val totalHeight = crossAxisPositions.lastOrNull()?.let {
            it + crossAxisSizes.lastOrNull()!!
                ?: 0
        } ?: 0

        // Set size of the layout
        layout(constraints.maxWidth, totalHeight) {
            // Place children
            sequences.forEachIndexed { sequenceIndex, placeables ->
                var mainAxisPosition = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(
                        x = mainAxisPosition,
                        y = crossAxisPositions[sequenceIndex]
                    )
                    mainAxisPosition += placeable.width + mainAxisSpacing.roundToPx()
                }
            }
        }
    }
}

@Composable
fun CheckboxRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    stars: Int = 5
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..stars) {
            IconButton(
                onClick = { onRatingChanged(i.toFloat()) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star $i",
                    tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.12f
                    )
                )
            }
        }
    }
}