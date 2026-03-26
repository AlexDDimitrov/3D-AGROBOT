package ui.wizard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import  androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.a3d_agrobot_app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GardenData(
    val id: Int = 0,
    val garden_name: String = "",
    val garden_width: Int = 0,
    val garden_height: Int = 0,
    val path_width: Int = 0,
    val number_beds: Int = 0,
    val plant: String = ""
)

@Composable
fun GardenPageApp(
    gardens: List<GardenData> = emptyList(),
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var gardens by remember { mutableStateOf<List<GardenData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = withContext(Dispatchers.IO) { TokenStore.getToken(context) } ?: return@LaunchedEffect
        gardens = withContext(Dispatchers.IO) {  }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6D11)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text ="Създай градини",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        when {
            loading ->
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center)
                {
                CircularProgressIndicator(color = Color(0xFF3B6D11))
            }
            gardens.isEmpty() ->
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center)
                {
                    Text(
                        "Няма добавени градини",
                        color = Color(0xFF639922).copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            else -> LazyColumn {
                itemsIndexed(gardens) { index, garden ->
                    GardenListItem(
                        displayIndex = index + 1,
                        gardenName   = garden.garden_name,
                        onEditClick  = { onEditClick(garden) }
                    )

                }
        }
    }
}

@Composable
fun GardenListItem(displayIndex: Int, gardenName: String, onEditClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xCCF4FAE8))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$displayIndex. $gardenName",
            color = Color(0xFF27500A),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = onEditClick) {
            Icon(
                painter = painterResource(id = R.drawable.edit_icon),
                contentDescription = "Edit",
                tint = Color(0xFF3B6D11)
            )
        }
    }
}