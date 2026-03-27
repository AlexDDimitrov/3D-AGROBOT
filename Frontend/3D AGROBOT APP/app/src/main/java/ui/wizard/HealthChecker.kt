package ui.wizard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a3d_agrobot_app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HealthCheckerScreen() {
    val context = LocalContext.current
    var reports by remember { mutableStateOf<List<ReportData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        val token = withContext(Dispatchers.IO) {
            TokenStore.getToken(context)
        }
        if (token == null) {
            error = "No token"
            loading = false
            return@LaunchedEffect
        }
        try {
            reports = withContext(Dispatchers.IO) { ReportRepository().getReports(token) }
        } catch (e: Exception) {
            error = "Грешка: ${e.message}"
        }
        loading = false
    }

    val sickReports = reports.filter { it.health == "sick" || it.health == "ill" }
    val allHealthy = reports.isNotEmpty() && sickReports.isEmpty()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5E9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Моите растения",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A3207)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF436B1F))
                    }
                }

                error.isNotEmpty() -> {
                    Text(error, color = Color(0xFFE57373))
                }

                allHealthy -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF3B6D11).copy(alpha = 0.1f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Всички растения са здрави",
                            color = Color(0xFF3B6D11),
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                reports.isEmpty() -> {
                    Text(
                        text = "Няма налични репорти",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(reports) { report ->
                            ReportCard(report)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ReportCard(report: ReportData) {
    val isSick = report.health == "sick" || report.health == "ill"
    val cardColor = when {
        !report.hasPlant -> Color.Gray
        isSick -> Color(0xFFE57373)
        else -> Color(0xFF3B6D11)
    }
    val backgroundColor = cardColor.copy(alpha = 0.08f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.health_icon),
                    contentDescription = null,
                    tint = cardColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = report.plantType ?: "Непознато растение",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A3207)
                )
                Text(
                    text = when {
                        !report.hasPlant -> "Няма засадено растение"
                        isSick -> "Болно"
                        else-> "Здраво"
                    },
                    fontSize = 13.sp,
                    color = cardColor
                )
            }
        }

        if (report.issues.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Проблеми", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Gray)
            report.issues.forEach { issue ->
                Text("• $issue", fontSize = 13.sp, color = Color(0xFF1A3207))
            }
        }

        if (report.recommendations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Препоръки", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Gray)
            report.recommendations.forEach { rec ->
                Text("• $rec", fontSize = 13.sp, color = Color(0xFF3B6D11))
            }
        }

        // Дата
        report.receivedAt?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, fontSize = 11.sp, color = Color.LightGray)
        }
    }
}



