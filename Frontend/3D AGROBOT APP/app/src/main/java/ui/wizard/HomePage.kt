package ui.wizard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.example.a3d_agrobot_app.R

@Composable
fun HomePageApp(onLogout: () -> Unit = {}) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gardenScreen by remember { mutableStateOf("list") }
    var selectedGarden by remember { mutableStateOf<GardenData?>(null) }

    LaunchedEffect(Unit) {
        firstName = withContext(Dispatchers.IO) { TokenStore.getFirstName(context) ?: "" }
        lastName = withContext(Dispatchers.IO) { TokenStore.getLastName(context) ?: "" }
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4FAE8)),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Добре дошли, $firstName $lastName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF27500A)
                )
                IconButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        TokenStore.clearToken(context)
                        withContext(Dispatchers.Main) { onLogout() }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        tint = Color(0xFF27500A)
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { values ->
        Box(modifier = Modifier.padding(values)) {
            when (selectedTab) {
                0 -> when (gardenScreen) {
                    "list" -> GardenPageApp(
                        onAddClick  = { gardenScreen = "create" },
                        onEditClick = { garden ->
                            selectedGarden = garden
                            gardenScreen = "edit"
                        }
                    )
                    "create" -> CreateGardenPageApp(
                        onSuccess = { gardenScreen = "list" },
                        onBack    = { gardenScreen = "list" }
                    )
                    "edit" -> selectedGarden?.let { garden ->
                        EditGardenPageApp(
                            garden    = garden,
                            onSuccess = { gardenScreen = "list" },
                            onBack    = { gardenScreen = "list" }
                        )
                    }
                }
        //        1 -> RobotConnectPageApp()
      //          2 -> CheckHealthPageApp()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavIconItem(
            iconId = R.drawable.wheat_icon,
            label = "Градини",
            isSelected = selectedTab == 0,
            onClick = {onTabSelected(0)
            }
        )
        NavIconItem(
            iconId = R.drawable.robot,
            label = "Робот",
            isSelected = selectedTab == 1,
            onClick = {onTabSelected(1)}
        )
        NavIconItem(
            iconId = R.drawable.health_icon,
            label = "Здраве",
            isSelected = selectedTab == 2,
            onClick = {onTabSelected(2)}
        )
    }
}

@Composable
fun NavIconItem(iconId: Int, label: String, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.5.dp)
                .clip(RoundedCornerShape(bottomStart =  4.dp, bottomEnd = 4.dp))
                .background(if (isSelected) Color(0xFF3B6D11) else Color.Transparent)

        )
        Spacer(modifier = Modifier.height(6.dp))
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = "Icon",
            tint = if (isSelected) Color(0xFF3B6D11) else Color(0xFF9db88a),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Color(0xFF3B6D11) else Color(0xFF9db88a)
        )

    }
}