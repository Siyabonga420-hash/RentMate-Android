package com.example.rentmate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private data class RentMateActivityItem(val category: String, val title: String, val detail: String)

private fun activityText(obj: JSONObject, vararg keys: String): String {
    for (key in keys) {
        val value = obj.optString(key, "").trim()
        if (value.isNotEmpty() && value != "null") return value
    }
    return ""
}

@Composable
fun RentMateActivityScreen(onBackClick: () -> Unit) {
    val repo = remember { RentMateRepository() }
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var entries by remember { mutableStateOf<List<RentMateActivityItem>>(emptyList()) }
    var message by remember { mutableStateOf("") }
    val green = Color(0xFF00813D)
    val navy = Color(0xFF2D3142)

    suspend fun refresh() {
        loading = true
        val results = mutableListOf<RentMateActivityItem>()
        val errors = mutableListOf<String>()
        coroutineScope {
            val sources = listOf(
                "Payments" to async { repo.getPayments() },
                "Chores" to async { repo.getChores() },
                "Announcements" to async { repo.getAnnouncements() },
                "Events" to async { repo.getEvents() }
            )
            sources.forEach { (category, deferred) ->
                try {
                    val array: JSONArray = deferred.await()
                    for (i in 0 until array.length()) {
                        val obj = array.optJSONObject(i) ?: continue
                        val title = when (category) {
                            "Payments" -> activityText(obj, "description", "reference", "payment_reference", "status").ifBlank { "Rent payment" }
                            "Chores" -> activityText(obj, "title", "name").ifBlank { "Household chore" }
                            "Announcements" -> activityText(obj, "title", "subject", "message").ifBlank { "Household announcement" }
                            else -> activityText(obj, "title", "name").ifBlank { "Household event" }
                        }
                        val detail = when (category) {
                            "Payments" -> listOf(activityText(obj, "amount", "amount_paid"), activityText(obj, "status"), activityText(obj, "paid_at", "created_at")).filter { it.isNotBlank() }.joinToString(" · ")
                            "Chores" -> listOf(activityText(obj, "status"), activityText(obj, "due_at", "due_date")).filter { it.isNotBlank() }.joinToString(" · ")
                            "Announcements" -> activityText(obj, "message", "body", "created_at")
                            else -> activityText(obj, "starts_at", "start_at", "date")
                        }
                        results.add(RentMateActivityItem(category, title, detail))
                    }
                } catch (e: Exception) {
                    errors.add(category)
                }
            }
        }
        entries = results
        message = if (errors.isNotEmpty()) "Could not load: ${errors.joinToString()}. Pull up this screen and tap Refresh to try again." else ""
        loading = false
    }

    LaunchedEffect(Unit) { refresh() }

    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F8)).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBackClick) { Text("Back", color = green) }
            TextButton(onClick = { if (!loading) scope.launch { refresh() } }, enabled = !loading) { Text("Refresh", color = green) }
        }
        Text("Activity", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = navy)
        Text("Your household updates", color = Color.Gray)
        Spacer(Modifier.height(20.dp))
        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth(), color = green)
        } else {
            if (message.isNotEmpty()) {
                Text(message, color = Color(0xFFB45309), style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
            }
            if (entries.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.fillMaxWidth().padding(24.dp)) {
                        Text("No activity available", fontWeight = FontWeight.Bold, color = navy)
                        Spacer(Modifier.height(8.dp))
                        Text("Payments, chores, announcements and events will appear here when available.")
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(entries) { item ->
                        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                                Text(item.category.uppercase(), color = green, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(5.dp))
                                Text(item.title, fontWeight = FontWeight.SemiBold, color = navy)
                                if (item.detail.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(item.detail, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
