package com.example.sneakhead.view
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sneakhead.repository.UserRepositoryImplementation
import com.example.sneakhead.viewmodel.UserViewModel


class ForgetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgetPasswordBody()

        }
    }
}

@Composable
fun ForgetPasswordBody(){
    var email by remember { mutableStateOf("") }
    val repo= remember { UserRepositoryImplementation() }
    val userViewModel = remember { UserViewModel(repo)}

    val context = LocalContext.current
    val activity = context as Activity

    Scaffold { padding ->

        Column(
            modifier = Modifier.fillMaxWidth().padding(padding)
        ) {
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    userViewModel.forgetPassword(email){
                            success,message->
                        if(success){
                            Toast.makeText(context,message, Toast.LENGTH_LONG).show()
                            activity?.finish()


                        }else{
                            Toast.makeText(context,message,Toast.LENGTH_LONG).show()

                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Submit")
            }

        }
    }

}

@Preview
@Composable
fun PreviewForgetBody(){
    ForgetPasswordBody()
}