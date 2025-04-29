package com.example.greenenergyapp.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.greenenergyapp.HomeActivity
import com.example.greenenergyapp.ProfileActivity
import com.example.greenenergyapp.User
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import com.example.greenenergyapp.ui.theme.backgroundLightGreen
import com.example.greenenergyapp.ui.theme.onPrimaryColor
import com.example.greenenergyapp.ui.theme.onSurfaceColor
import com.example.greenenergyapp.ui.theme.primaryGreen
import com.example.greenenergyapp.ui.theme.secondaryGreen
import com.example.greenenergyapp.ui.theme.surfaceGreen
import com.example.greenenergyapp.ui.theme.tertiaryGreen



@Preview
@Composable
fun HelpSupportScreenPreview() {
    GreenEnergyAppTheme {
        HelpSupportScreen(user = User(-1, "dummy@email.com", "1234", 25.36f, 25.36f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(user:User) {
    val viewModel = remember { HelpSupportViewModel() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Create a custom color scheme for Green Energy App
    val colorScheme = darkColorScheme(
        primary = primaryGreen,
        secondary = secondaryGreen,
        tertiary = tertiaryGreen,
        background = backgroundLightGreen,
        surface = surfaceGreen,
        onPrimary = onPrimaryColor,
        onSecondary = onPrimaryColor,
        onBackground = onSurfaceColor,
        onSurface = onSurfaceColor,
        onTertiary = onPrimaryColor
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Help & Support",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            val intent = Intent(context, ProfileActivity::class.java)
                            intent.putExtra("userId", user.userId)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary
                    )
                )
            },
            containerColor = colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // FAQ Section
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // FAQ Items
                FaqItem(
                    question = "How do I reset my password?",
                    answer = "To reset your password, go to the login screen and tap on 'Forgot Password'. Enter your email address and follow the instructions sent to your inbox.",
                    colorScheme = colorScheme
                )

                Spacer(modifier = Modifier.height(16.dp))

                FaqItem(
                    question = "How do I update my account information?",
                    answer = "You can update your account information by navigating to the Profile section and tapping on 'Edit Profile'. From there, you can modify your personal details, contact information, and preferences.",
                    colorScheme = colorScheme
                )

                Spacer(modifier = Modifier.height(16.dp))

                FaqItem(
                    question = "How do I cancel my subscription?",
                    answer = "To cancel your subscription, go to Account Settings > Subscriptions > Manage. Select your active subscription and tap on 'Cancel Subscription'. Follow the prompts to complete the cancellation process.",
                    colorScheme = colorScheme
                )

                Spacer(modifier = Modifier.height(16.dp))

                FaqItem(
                    question = "Is my payment information secure?",
                    answer = "Yes, we use industry-standard encryption and security protocols to protect your payment information. We do not store your full credit card details on our servers. All transactions are processed through secure payment gateways.",
                    colorScheme = colorScheme
                )

                Spacer(modifier = Modifier.height(16.dp))

                FaqItem(
                    question = "How do I contact customer support?",
                    answer = "You can contact our customer support team through this form below, by email at support@example.com, or by phone at 1-800-123-4567 during business hours (Monday-Friday, 9AM-5PM EST).",
                    colorScheme = colorScheme
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Contact Support Section
                Text(
                    text = "Contact Support",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contact Support Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Name Field
                        OutlinedTextField(
                            value = viewModel.name,
                            onValueChange = { viewModel.name = it },
                            label = { Text("Your Name", color = colorScheme.onSurface) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.White.copy(alpha = 0.7f),
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.tertiary,
                                cursorColor = colorScheme.primary
                            )
                        )

                        // Email Field
                        OutlinedTextField(
                            value = viewModel.email,
                            onValueChange = { viewModel.email = it },
                            label = { Text("Email Address", color = colorScheme.onSurface) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.White.copy(alpha = 0.7f),
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.tertiary,
                                cursorColor = colorScheme.primary
                            )
                        )

                        // Issue Type Dropdown
                        ExposedDropdownMenuBox(
                            expanded = viewModel.isDropdownExpanded,
                            onExpandedChange = { viewModel.isDropdownExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = viewModel.selectedIssueType ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Issue Type", color = colorScheme.onSurface) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Dropdown",
                                        tint = colorScheme.primary
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color.White.copy(alpha = 0.7f),
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.tertiary,
                                    cursorColor = colorScheme.primary
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = viewModel.isDropdownExpanded,
                                onDismissRequest = { viewModel.isDropdownExpanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                viewModel.issueTypes.forEach { issueType ->
                                    DropdownMenuItem(
                                        text = { Text(issueType, color = colorScheme.onSurface) },
                                        onClick = {
                                            viewModel.selectedIssueType = issueType
                                            viewModel.isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Message Field
                        OutlinedTextField(
                            value = viewModel.message,
                            onValueChange = { viewModel.message = it },
                            label = { Text("Message", color = colorScheme.onSurface) },
                            placeholder = {
                                Text(
                                    "Please describe your issue in detail...",
                                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.White.copy(alpha = 0.7f),
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.tertiary,
                                cursorColor = colorScheme.primary
                            )
                        )

                        // Submit Button
                        Button(
                            onClick = { viewModel.submitForm() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary
                            )
                        ) {
                            Text("Submit", color = colorScheme.onPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String, colorScheme: ColorScheme) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    color = colorScheme.primary
                )

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = colorScheme.primary
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface
                )
            }
        }
    }
}

// ViewModel for Help & Support Screen
class HelpSupportViewModel {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var selectedIssueType by mutableStateOf<String?>(null)
    var message by mutableStateOf("")
    var isDropdownExpanded by mutableStateOf(false)

    val issueTypes = listOf(
        "Technical Issue",
        "Billing Question",
        "Account Problem",
        "Feature Request",
        "Other"
    )

    fun submitForm() {
        // Implement form submission logic
        println("Submitted form with: Name=$name, Email=$email, Issue=$selectedIssueType, Message=$message")

        // Reset form after submission (or handle according to your app's needs)
        // name = ""
        // email = ""
        // selectedIssueType = null
        // message = ""
    }
}