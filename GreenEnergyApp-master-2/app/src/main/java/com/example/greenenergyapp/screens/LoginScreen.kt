package com.example.greenenergyapp.screens


import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greenenergyapp.AppDataBaseRepository
import com.example.greenenergyapp.ForgotPasswordActivity
import com.example.greenenergyapp.HomeActivity
import com.example.greenenergyapp.R
import com.example.greenenergyapp.SignUpActivity
import com.example.greenenergyapp.ui.theme.GreenEnergyAppTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(context: Context) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val userRep = AppDataBaseRepository.getInstance(context).getUserDAO()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 24.dp)
            )

            // Title
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Login to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Email Input
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

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input with Toggle Visibility
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {

                    // Launch a coroutine to call the suspend function
                    scope.launch {
                        val checkedUser = userRep.getUserByEmailAndPassword(email, password)
                        // Handle login result here
                        if (checkedUser != null) {
                            // Login successful
                            // Navigate to next screen or update UI
                            val intent=Intent(context,HomeActivity::class.java)
                            intent.putExtra("userId",checkedUser.userId)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show()
                            email = ""
                            password=""
                        }

                        val sharedPref = context.getSharedPreferences("GreenEnergyPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            if (checkedUser != null) {
                                putInt("userId", checkedUser.userId)
                            }  // 'loggedUser' is the user object retrieved after a successful login
                            apply()
                        }


                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Log In", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign-Up Prompt
            TextButton(onClick = {
                // Navigate to sign-up screen
                context.startActivity(Intent(context, SignUpActivity::class.java))
            }) {
                Text("Don't have an account? Sign up", color = MaterialTheme.colorScheme.primary)
            }// Sign-Up Prompt
            TextButton(onClick = {
                // Navigate to Forgot password screen
                context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
            }) {
                Text("Forgot your password ?", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    GreenEnergyAppTheme {
        LoginScreen(context = LocalContext.current)
    }

}
