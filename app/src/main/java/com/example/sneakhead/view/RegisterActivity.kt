package com.example.sneakhead.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person

import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sneakhead.R
import com.example.sneakhead.model.UserModel
import com.example.sneakhead.repository.UserRepositoryImplementation
import com.example.sneakhead.viewmodel.UserViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBody() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val repo = remember { UserRepositoryImplementation() }
    val userViewModel = remember { UserViewModel(repo) }
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5E6D3), // Light beige at top
                        Color(0xFF2D2D2D)  // Dark at bottom
                    ),
                    startY = 0f,
                    endY = 800f
                )
            )
    ) {
        // Logo in top left corner
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Shoe Image in top center
            Image(
                painter = painterResource(R.drawable.loginshoe),
                contentDescription = "Shoe illustration",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title Text
            Text(
                text = "Lace up. The world\nis your runway.",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Full Name Field
            OutlinedTextField(
                value = "$firstName $lastName".trim(),
                onValueChange = {
                    val names = it.split(" ", limit = 2)
                    firstName = names.getOrNull(0) ?: ""
                    lastName = names.getOrNull(1) ?: ""
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Full Name",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF3A3A3A),
                    unfocusedContainerColor = Color(0xFF3A3A3A),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF6B35)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Email",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF3A3A3A),
                    unfocusedContainerColor = Color(0xFF3A3A3A),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF6B35)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Password",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) R.drawable.baseline_remove_red_eye_24 
                                else R.drawable.baseline_visibility_off_24
                            ),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF3A3A3A),
                    unfocusedContainerColor = Color(0xFF3A3A3A),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF6B35)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Confirm Password",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (confirmPasswordVisible) R.drawable.baseline_remove_red_eye_24 
                                else R.drawable.baseline_visibility_off_24
                            ),
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF3A3A3A),
                    unfocusedContainerColor = Color(0xFF3A3A3A),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF6B35)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gender Section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Gender",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "Male",
                            onClick = { gender = "Male" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFFF6B35),
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            "Male",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "Female",
                            onClick = { gender = "Female" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFFF6B35),
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            "Female",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "Other",
                            onClick = { gender = "Other" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFFF6B35),
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            "Other",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Terms and Conditions Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFF6B35),
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I agree to the Terms of Service and Privacy Policy.",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Kickstart Button
            Button(
                onClick = {
                    // Validation - check required fields with specific error messages
                    when {
                        firstName.isBlank() -> {
                            Toast.makeText(context, "Please enter your first name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        lastName.isBlank() -> {
                            Toast.makeText(context, "Please enter your last name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        email.isBlank() -> {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        password.isBlank() -> {
                            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        password.length < 6 -> {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        confirmPassword.isBlank() -> {
                            Toast.makeText(context, "Please confirm your password", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        gender.isBlank() -> {
                            Toast.makeText(context, "Please select your gender", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        !acceptedTerms -> {
                            Toast.makeText(context, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }

                    userViewModel.register(email, password) { success, message, userID ->
                        if (success) {
                            val userModel = UserModel(
                                userID = userID,
                                email = email,
                                firstName = firstName,
                                lastName = lastName,
                                gender = gender
                            )
                            userViewModel.addUserToDatabase(userID, userModel) { dbSuccess, dbMessage ->
                                Toast.makeText(context, dbMessage, Toast.LENGTH_LONG).show()
                                if (dbSuccess) {
                                    val intent = Intent(context, LoginActivity::class.java)
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                            }
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Kickstart ✨",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Link
            Text(
                text = "Already have an account? Log in",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterBody()
}