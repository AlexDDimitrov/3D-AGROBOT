package ui.wizard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.a3d_agrobot_app.ui.theme._3D_AGROBOT_APPTheme

class WelcomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _3D_AGROBOT_APPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    WelcomePageApp(
                    )
                }
            }
        }
    }
}


@Composable
fun WelcomePageApp() {

}
