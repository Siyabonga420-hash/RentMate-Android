package com.example.rentmate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun GroceriesScreen(onBack: () -> Unit) {

    val context = LocalContext.current
    val repo = remember { RentMateRepository() }
    val scope = rememberCoroutineScope()

    var records by remember { mutableStateOf<List<JSONObject>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var neededAt by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }

    suspend fun refresh() {
        loading = true
        error = ""

        try {
            val response = repo.getGroceries()

            records = (0 until response.length()).mapNotNull {
                response.optJSONObject(it)
            }

        } catch (e: Exception) {
            error = e.message ?: "Could not load groceries."
        }

        loading = false
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Text(
            text = "Shared Groceries",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Your household shopping list.")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showAdd = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00813D)
            )
        ) {
            Text("+ Add Item")
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        if (error.isNotBlank()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        if (!loading && error.isBlank() && records.isEmpty()) {
            Text("Your shopping list is empty.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(records) { item ->

                val purchased = item.optBoolean("purchased", false)

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(Modifier.padding(16.dp)) {

                        Text(
                            item.optString("title", "Untitled"),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(8.dp))
                        val schedule = item.optString("needed_at")
                        if (schedule.isNotBlank() && schedule != "null") {
                            Text("Needed by: ${formatRentMateDateTime(schedule)}")
                            Spacer(Modifier.height(6.dp))
                        }

                        Text(
                            if (purchased) "Purchased" else "Still needed",
                            color = if (purchased) {
                                Color(0xFF00813D)
                            } else {
                                Color(0xFFF56038)
                            }
                        )

                        if (!purchased) {

                            TextButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            repo.purchaseGrocery(
                                                item.optString("id")
                                            )
                                            refresh()
                                        } catch (e: Exception) {
                                            error = e.message ?: "Update failed."
                                        }
                                    }
                                }
                            ) {
                                Text("Mark as Purchased")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {

        AlertDialog(
            onDismissRequest = {
                if (!saving) showAdd = false
            },

            title = { Text("Add Grocery") },

            text = {
                Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item name") }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { pickRentMateDateTime(context) { neededAt = it } }) {
                    Text(if (neededAt.isBlank()) "Set Needed by (optional)" else formatRentMateDateTime(neededAt))
                }
                if (neededAt.isNotBlank()) TextButton(onClick = { neededAt = "" }) { Text("Clear date") }
                }
            },

            confirmButton = {

                Button(
                    enabled = title.isNotBlank() && !saving,

                    onClick = {

                        scope.launch {

                            saving = true

                            try {
                                repo.addGrocery(title.trim(), neededAt.ifBlank { null })
                                title = ""
                                neededAt = ""
                                showAdd = false
                                refresh()
                            } catch (e: Exception) {
                                error = e.message ?: "Could not add item."
                            }

                            saving = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { showAdd = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
