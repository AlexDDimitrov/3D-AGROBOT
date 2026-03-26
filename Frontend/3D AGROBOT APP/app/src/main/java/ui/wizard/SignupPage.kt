package ui.wizard

import android.R.attr.icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a3d_agrobot_app.R
import com.example.a3d_agrobot_app.ui.theme._3D_AGROBOT_APPTheme

class SignupPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _3D_AGROBOT_APPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    SignupPageApp(
                    )
                }
            }
        }
    }
}

@Composable
fun SignupPageApp() {
    var first_name by remember { mutableStateOf(String()) }
    var last_name by remember { mutableStateOf(String()) }
    var email by remember { mutableStateOf(String()) }
    var isValid by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf(String()) }
    var passwordVisibility by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = first_name,
        label = { Text("Име")},
        placeholder = { Text("Въведете първото си име")},
        onValueChange = {new_name -> first_name = new_name },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    )
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = last_name,
        label = { Text("Фамилия")},
        placeholder = { Text("Въведете фамилията си")},
        onValueChange = {new_name -> last_name = new_name },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    )
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = email,
        label = { Text("Имейл")},
        placeholder = { Text("Въведете имейла си")},
        onValueChange = {
            new_email -> email = new_email
            isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(new_email).matches()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )

    )
    if (!isValid) {
        Text("Невалиден мейл")
    }
    Spacer(modifier = Modifier.height(20.dp))

    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.visibility_icon)
    else
        painterResource(id = R.drawable.visibility_icon_off)

    OutlinedTextField(
        value = password,
        onValueChange = {
            newPassword -> password = newPassword
        },
        placeholder = { Text(text = "Парола") },
        trailingIcon = {
            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(
                    painter = icon,
                    contentDescription = "Visibility icon"
                )
            }
        },
        label = { Text(text = "Въведете паролата си") },
                keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (passwordVisibility)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        )
    Button(
        onClick = {
// TO DO
        },

    ) {
        Text(
            text = "Направете си акаунт"
        )
    }
}

