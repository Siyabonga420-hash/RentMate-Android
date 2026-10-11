package com.example.rentmate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
fun AccommodationPageScreen(
    onBack: () -> Unit = {},
    onRequestJoin: () -> Unit = {}
) {
    val bronze = Color(0xFF8A5A2B)
    val bgLight = Color(0xFFF7F5F1)
    val textSecondary = Color(0xFF6B6459)

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("← Back to results", fontSize = 13.sp, color = textSecondary,
                modifier = Modifier.clickable { onBack() })
            Text("♡", fontSize = 18.sp, color = textSecondary)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFEDEAE2)),
            contentAlignment = Alignment.Center
        ) {
            Text("Photo", fontSize = 14.sp, color = textSecondary)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("1 / 6 photos", fontSize = 11.sp, color = textSecondary)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Sunny Apartments, Unit 4B", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text("Braamfontein, Johannesburg", fontSize = 13.sp, color = textSecondary)
                }
                Text("R4,200/mo", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("2 bed", fontSize = 13.sp, color = textSecondary)
                Text("1 bath", fontSize = 13.sp, color = textSecondary)
                Text("Fits 3", fontSize = 13.sp, color = textSecondary)
                Text("65 m²", fontSize = 13.sp, color = textSecondary)
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgLight)
                    .padding(14.dp)
            ) {
                Text("About this place", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Bright shared apartment close to campus, walking distance to shops and transport. Fibre-ready, secure parking, communal laundry.",
                    fontSize = 13.sp, color = textSecondary
                )
            }

            Spacer(Modifier.height(16.dp))

            val amenities = listOf("Fibre included", "Secure parking", "Pet friendly", "Furnished")
            Column {
                amenities.chunked(2).forEach { pair ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        pair.forEach {
                            Text("✓ $it", fontSize = 12.5.sp, color = textSecondary)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEDEAE2)),
                contentAlignment = Alignment.Center
            ) {
                Text("Map preview", fontSize = 12.5.sp, color = textSecondary)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDEAE2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("T", fontSize = 14.sp, color = textSecondary)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Listed by Thandi M.", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Property manager", fontSize = 12.sp, color = textSecondary)
                    }
                }
                Button(
                    onClick = onRequestJoin,
                    colors = ButtonDefaults.buttonColors(containerColor = bronze)
                ) {
                    Text("Request to join", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 850)
@Composable
fun AccommodationPageScreenPreview() {
    MaterialTheme {
        AccommodationPageScreen()
    }
}