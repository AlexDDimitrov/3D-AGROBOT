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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    onEditClick: (GardenData) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var gardens by remember { mutableStateOf<List<GardenData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token =
            withContext(Dispatchers.IO) { TokenStore.getToken(context) } ?: return@LaunchedEffect
        gardens = withContext(Dispatchers.IO) { GardenRepository().getGardens(token) }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6D11)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Създай градини",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        when {
            loading ->
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                )
                {
                    CircularProgressIndicator(color = Color(0xFF3B6D11))
                }

            gardens.isEmpty() ->
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                )
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
                        gardenName = garden.garden_name,
                        onEditClick = { onEditClick(garden) }
                    )
                }
            }
        }
    }
}

@Composable
fun GardenListItem(displayIndex: Int, gardenName: String, onEditClick: () -> Unit) {
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

@Composable
fun CreateGardenPageApp(onSuccess: () -> Unit, onBack: () -> Unit ) {
val context = LocalContext.current
val scope = rememberCoroutineScope ()

var gardenName   by rememberSaveable { mutableStateOf("") }
var width        by rememberSaveable { mutableStateOf("") }
var height       by rememberSaveable { mutableStateOf("") }
var pathWidth    by rememberSaveable { mutableStateOf("") }
var beds         by rememberSaveable { mutableStateOf("") }
var plant        by rememberSaveable { mutableStateOf("") }
var statusMessage    by rememberSaveable { mutableStateOf("") }
var isError      by rememberSaveable { mutableStateOf(false) }
var isLoading    by rememberSaveable { mutableStateOf(false) }

val textFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor     = Color(0xFF3B6D11),
    unfocusedBorderColor   = Color(0xFF639922).copy(alpha = 0.5f),
    focusedLabelColor      = Color(0xFF3B6D11),
    unfocusedLabelColor    = Color(0xFF639922),
    cursorColor            = Color(0xFF3B6D11),
    focusedTextColor       = Color(0xFF27500A),
    unfocusedTextColor     = Color(0xFF27500A),
    unfocusedContainerColor= Color(0x22FFFFFF),
    focusedContainerColor  = Color(0x44FFFFFF),
)

val isFormFilled = gardenName.isNotBlank() && width.isNotBlank() &&
        height.isNotBlank() && pathWidth.isNotBlank() &&
        beds.isNotBlank() && plant.isNotBlank()

Column(
    modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF4FAE8))
        .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(id = R.drawable.back_icon),
                contentDescription = "Back",
                tint = Color(0xFF27500A)
            )
        }
        Text(
            "Нова градина",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF27500A)
        )
    }
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = gardenName, onValueChange = { gardenName = it },
        label = { Text("Име на градината") },
        singleLine = true, colors = textFieldColors,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = width, onValueChange = { width = it },
            label = { Text("Ширина(см)") },
            singleLine = true, colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = height, onValueChange = { height = it },
            label = { Text("Височина(см)") },
            singleLine = true, colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = pathWidth, onValueChange = { pathWidth = it },
            label = { Text("Ширина пътека (cм)") },
            singleLine = true, colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = beds, onValueChange = { beds = it },
            label = { Text("Брой лехи") },
            singleLine = true, colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = plant, onValueChange = { plant = it },
        label = { Text("Вид растение") },
        singleLine = true, colors = textFieldColors,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(24.dp))

    if (statusMessage.isNotEmpty()) {
        Text(
            text = statusMessage,
            color = if (isError) Color(0xFFE57373) else Color(0xFF3B6D11),
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
    Button(
        onClick = {
            isLoading = true
            scope.launch(Dispatchers.IO) {
                try {
                    val token = TokenStore.getToken(context) ?: throw Exception("Няма токен")
                    val code = GardenRepository().createGarden(
                        token,
                        GardenData(
                            garden_name   = gardenName,
                            garden_width  = width.toInt(),
                            garden_height = height.toInt(),
                            path_width    = pathWidth.toInt(),
                            number_beds   = beds.toInt(),
                            plant         = plant
                        )
                    )
                    withContext(Dispatchers.Main) {
                        if (code in 200..299) {
                            onSuccess()
                        } else {
                            statusMessage = "Грешка от сървъра: $code"
                            isError = true
                            isLoading = false
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        statusMessage = "Грешка: ${e.message}"
                        isError = true
                        isLoading = false
                    }
                }
            }
        },
        enabled = isFormFilled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3B6D11),
            contentColor   = Color(0xFFEAF3DE),
            disabledContainerColor = Color(0xFF3B6D11).copy(alpha = 0.5f),
            disabledContentColor   = Color(0xFFEAF3DE).copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(50.dp)
    ) {
        if (isLoading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        else Text("Създай градина", fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
}

@Composable
fun EditGardenPageApp(garden: GardenData, onSuccess: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var gardenName by rememberSaveable { mutableStateOf(garden.garden_name) }
    var width by rememberSaveable { mutableStateOf(garden.garden_width.toString()) }
    var height by rememberSaveable { mutableStateOf(garden.garden_height.toString()) }
    var pathWidth by rememberSaveable { mutableStateOf(garden.path_width.toString()) }
    var beds by rememberSaveable { mutableStateOf(garden.number_beds.toString()) }
    var plant by rememberSaveable { mutableStateOf(garden.plant) }

    var statusMessage by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF3B6D11),
        unfocusedBorderColor = Color(0xFF639922).copy(alpha = 0.5f),
        focusedLabelColor = Color(0xFF3B6D11),
        unfocusedLabelColor = Color(0xFF639922),
        cursorColor = Color(0xFF3B6D11),
        focusedTextColor = Color(0xFF27500A),
        unfocusedTextColor = Color(0xFF27500A),
        unfocusedContainerColor = Color(0x22FFFFFF),
        focusedContainerColor = Color(0x44FFFFFF),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF4FAE8)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back",
                    tint = Color(0xFF27500A)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = gardenName, onValueChange = { gardenName = it },
            label = { Text("Име на градината") },
            singleLine = true, colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = plant, onValueChange = { plant = it },
            label = { Text("Вид растение") },
            singleLine = true, colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = beds, onValueChange = { beds = it },
            label = { Text("Брой лехи") },
            singleLine = true, colors = textFieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (statusMessage.isNotEmpty()) {
            Text(
                statusMessage,
                color = if (isError) Color(0xFFE57373) else Color(0xFF3B6D11),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                isLoading = true
                scope.launch(Dispatchers.IO) {
                    try {
                        val token = TokenStore.getToken(context) ?: throw Exception("Няма токен")
                        val updatedData = GardenData(
                            id = garden.id,
                            garden_name = gardenName,
                            garden_width = width.toInt(),
                            garden_height = height.toInt(),
                            path_width = pathWidth.toInt(),
                            number_beds = beds.toInt(),
                            plant = plant
                        )
                        val code = GardenRepository().editGarden(token, garden.id, updatedData)

                        withContext(Dispatchers.Main) {
                            if (code in 200..299) {
                                onSuccess()
                            } else {
                                statusMessage = "Грешка от сървъра: $code"
                                isError = true
                                isLoading = false
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            statusMessage = "Грешка: ${e.message}"
                            isError = true
                            isLoading = false
                        }
                    }
                }
            },
            enabled = gardenName.isNotBlank() && plant.isNotBlank() && beds.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B6D11),
                contentColor = Color(0xFFEAF3DE),
                disabledContainerColor = Color(0xFF3B6D11).copy(alpha = 0.5f),
                disabledContentColor = Color(0xFFEAF3DE).copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
            else Text("Запази промените", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

