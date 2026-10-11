package com.example.rentmate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun HouseholdEventsScreen(onBack: () -> Unit) {
    val repo = remember { RentMateRepository() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var events by remember { mutableStateOf<List<JSONObject>>(emptyList()) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var adding by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    suspend fun refresh() {
        loading = true
        try {
            val response = repo.getEvents()
            events = (0 until response.length()).mapNotNull { response.optJSONObject(it) }
            error = ""
        } catch (e: Exception) { error = e.message ?: "Could not load events." }
        loading = false
    }
    LaunchedEffect(Unit) { refresh() }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("Household Events", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Plan events together with your housemates.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { adding = true }) { Text("+ New Event") }
        if (loading) CircularProgressIndicator()
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        if (!loading && events.isEmpty() && error.isBlank()) Text("No household events yet.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(events) { item ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.optString("title", "Event"), style = MaterialTheme.typography.titleMedium)
                        Text(formatRentMateDateTime(item.optString("starts_at")))
                    }
                }
            }
        }
    }
    if (adding) AlertDialog(
        onDismissRequest = { if (!saving) adding = false },
        title = { Text("New Household Event") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Event title") })
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { pickRentMateDateTime(context) { start = it } }) {
                    Text(if (start.isBlank()) "Choose date & time" else formatRentMateDateTime(start))
                }
            }
        },
        confirmButton = {
            Button(enabled = !saving && title.isNotBlank() && start.isNotBlank(), onClick = {
                scope.launch {
                    saving = true
                    try { repo.addEvent(title, start); title = ""; start = ""; adding = false; refresh() }
                    catch (e: Exception) { error = e.message ?: "Could not add event." }
                    saving = false
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = { adding = false }) { Text("Cancel") } }
    )
}
