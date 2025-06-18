package com.example.sneakhead.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sneakhead.model.UserModel
import com.example.sneakhead.repository.UserRepositoryImplementation
import com.example.sneakhead.viewmodel.UserViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { padding ->
                RegisterBody(padding)
            }
        }
    }
}

@Composable
fun RegisterBody(paddingValues: PaddingValues) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val countries = listOf("USA", "India", "UK", "Germany", "Other")

    val repo = remember { UserRepositoryImplementation() }
    val userViewModel = remember { UserViewModel(repo) }
    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = firstName, onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        // Country Dropdown
        Box {
            OutlinedTextField(
                value = selectedCountry,
                onValueChange = {},
                label = { Text("Select Country") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null,
                        Modifier.clickable { expanded = true })
                },
                readOnly = true
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                countries.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = {
                        selectedCountry = it
                        expanded = false
                    })
                }
            }
        }

        OutlinedTextField(
            value = dob, onValueChange = { dob = it },
            label = { Text("Date of Birth") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("DD/MM/YYYY") }
        )

        Text("Gender")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = gender == "Male", onClick = { gender = "Male" })
            Text("Male", modifier = Modifier.padding(end = 8.dp))

            RadioButton(selected = gender == "Female", onClick = { gender = "Female" })
            Text("Female", modifier = Modifier.padding(end = 8.dp))

            RadioButton(selected = gender == "Other", onClick = { gender = "Other" })
            Text("Other")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it })
            Text("I accept the terms and conditions", modifier = Modifier.padding(start = 4.dp))
        }

        Button(
            onClick = {
                // Validation
                if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()
                    || selectedCountry.isBlank() || dob.isBlank() || gender.isBlank() || !acceptedTerms
                ) {
                    Toast.makeText(context, "Please fill all fields and accept terms", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                userViewModel.register(email, password) { success, message, userID ->
                    if (success) {
                        val userModel = UserModel(
                            userID = userID,
                            email = email,
                            firstName = firstName,
                            lastName = lastName,
                            gender = gender,
                            dob = dob,
                            country = selectedCountry
                        )
                        userViewModel.addUserToDatabase(userID, userModel) { dbSuccess, dbMessage ->
                            Toast.makeText(context, dbMessage, Toast.LENGTH_LONG).show()
                            if (dbSuccess) {
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
                .padding(vertical = 16.dp)
        ) {
            Text("Register")
        }

        TextButton(onClick = {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }) {
            Text("Already have an account? Sign in")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterBody(paddingValues = PaddingValues(0.dp))
}
