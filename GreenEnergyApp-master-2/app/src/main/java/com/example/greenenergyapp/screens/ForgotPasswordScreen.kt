package com.example.yourapp

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greenenergyapp.AppDataBaseRepository
import com.example.greenenergyapp.MailSender
import com.example.greenenergyapp.MainActivity
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    GreenEnergyAppTheme {
        ForgotPasswordScreen()
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen() {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var userDAO= remember{ AppDataBaseRepository.getInstance(context).getUserDAO()}

    val focusManager = LocalFocusManager.current

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Forgot Password",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We will send password to your email",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("example@domain.com") },
                singleLine = true,
                isError = !isValidEmail(email) && email.isNotEmpty(),
                supportingText = {
                    if (!isValidEmail(email) && email.isNotEmpty()) {
                        Text("Invalid email format")
                    }
                },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                trailingIcon = {
                    if (email.isNotEmpty()) {
                        IconButton(onClick = { email = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear email")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            val password = userDAO.getPasswordByEmail(email)
                            withContext(Dispatchers.Main) {
                                if (password != null) {
                                    val sender = MailSender(
                                        user = "greenenergysystemsapp@gmail.com",
                                        pass = "gdgcthpxsarzqctv"
                                    )

                                    sender.sendMail(
                                        toEmail = email,
                                        subject = "Password Reset",
                                        message = "Your password is: $password"
                                    ) { success, error ->
                                        if (success) {
                                            Handler(Looper.getMainLooper()).post {
                                                Toast.makeText(context, "Password Sent !!", Toast.LENGTH_SHORT).show()
                                            }
                                            email=""
                                        } else {
                                            Toast.makeText(context, "Could not send: $error", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Email could not found!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Send password",
                    fontSize = 16.sp
                )
            }
        }
    }
}


