package com.example.rentmate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun ChoresScreen(onBack: () -> Unit) {

    val repo = remember { RentMateRepository() }
    val scope = rememberCoroutineScope()

    var records by remember { mutableStateOf<List<JSONObject>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }

    suspend fun refresh() {
        loading = true
        error = ""

        try {
            val response = repo.getChores()

            records = (0 until response.length()).mapNotNull {
                response.optJSONObject(it)
            }

        } catch (e: Exception) {
            error = e.message ?: "Could not load chores."
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
            text = "Commune Chores",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Manage household responsibilities.")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showAdd = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00813D)
            )
        ) {
            Text("+ Add Chore")
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        if (error.isNotBlank()) {
            Text(error, color = MaterialTheme.colorScheme.error)

            TextButton(
                onClick = {
                    scope.launch { refresh() }
                }
            ) {
                Text("Retry")
            }
        }

        if (!loading && error.isBlank() && records.isEmpty()) {
            Text("No chores yet.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(records) { item ->

                val completed = item.optString("status")
                    .equals("completed", ignoreCase = true)

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(Modifier.padding(16.dp)) {

                        Text(
                            item.optString("title", "Untitled"),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            if (completed) "Completed" else "To do",
                            color = if (completed) {
                                Color(0xFF00813D)
                            } else {
                                Color(0xFFF56038)
                            }
                        )

                        if (!completed) {

                            TextButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            repo.completeChore(
                                                item.optString("id")
                                            )
                                            refresh()
                                        } catch (e: Exception) {
                                            error = e.message ?: "Update failed."
                                        }
                                    }
                                }
                            ) {
                                Text("Mark as Completed")
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

            title = { Text("Add Chore") },

            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Chore name") }
                )
            },

            confirmButton = {

                Button(
                    enabled = title.isNotBlank() && !saving,

                    onClick = {

                        scope.launch {

                            saving = true

                            try {
                                repo.createChore(title.trim())
                                title = ""
                                showAdd = false
                                refresh()
                            } catch (e: Exception) {
                                error = e.message ?: "Could not add chore."
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
