package com.example.rentmate

import android.app.DatePickerDialog
import android.app.TimePickerDialog

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

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun KitchenBookingScreen(onBack: () -> Unit) {

    val context = LocalContext.current
    val repo = remember { RentMateRepository() }
    val scope = rememberCoroutineScope()

    var records by remember { mutableStateOf<List<JSONObject>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    var showAdd by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }

    suspend fun refresh() {

        loading = true
        error = ""

        try {
            val response = repo.getBookings()

            records = (0 until response.length()).mapNotNull {
                response.optJSONObject(it)
            }

        } catch (e: Exception) {
            error = e.message ?: "Could not load bookings."
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
            text = "Kitchen & Braai",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text("Reserve your shared facilities.")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                title = ""
                start = ""
                end = ""
                showAdd = true
            }
        ) {
            Text("+ New Booking")
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        if (error.isNotBlank()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        if (!loading && error.isBlank() && records.isEmpty()) {
            Text("No bookings yet.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(records) { item ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(Modifier.padding(16.dp)) {

                        Text(
                            item.optString("title", "Booking"),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Starts: " + formatBookingDate(
                                item.optString("starts_at")
                            )
                        )

                        Text(
                            "Ends: " + formatBookingDate(
                                item.optString("ends_at")
                            )
                        )
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

            title = {
                Text("New Booking")
            },

            text = {

                Column {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Booking name") }
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            pickBookingDateTime(context) {
                                start = it
                            }
                        }
                    ) {
                        Text(
                            if (start.isBlank()) {
                                "Select Start"
                            } else {
                                "Start: ${formatBookingDate(start)}"
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            pickBookingDateTime(context) {
                                end = it
                            }
                        }
                    ) {
                        Text(
                            if (end.isBlank()) {
                                "Select End"
                            } else {
                                "End: ${formatBookingDate(end)}"
                            }
                        )
                    }

                    if (error.isNotBlank()) {
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },

            confirmButton = {

                Button(
                    enabled = !saving &&
                        title.isNotBlank() &&
                        start.isNotBlank() &&
                        end.isNotBlank(),

                    onClick = {

                        scope.launch {

                            saving = true

                            try {
                                repo.addBooking(
                                    title = title.trim(),
                                    start = start,
                                    end = end
                                )

                                showAdd = false
                                refresh()

                            } catch (e: Exception) {
                                error = e.message ?: "Booking failed."
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

private fun pickBookingDateTime(
    context: android.content.Context,
    onSelected: (String) -> Unit
) {

    val now = Calendar.getInstance()

    DatePickerDialog(
        context,

        { _, year, month, day ->

            TimePickerDialog(
                context,

                { _, hour, minute ->

                    val calendar = Calendar.getInstance()

                    calendar.set(
                        year,
                        month,
                        day,
                        hour,
                        minute
                    )

                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val formatter = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ssXXX",
                        Locale.US
                    )

                    onSelected(
                        formatter.format(calendar.time)
                    )
                },

                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true

            ).show()
        },

        now.get(Calendar.YEAR),
        now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH)

    ).show()
}

private fun formatBookingDate(value: String): String {

    return try {

        val normalized = value.replace(
            Regex("\\.(\\d+)(?=Z|[+-]\\d{2}:?\\d{2}$)")
        ) {
            "." + it.groupValues[1].padEnd(3, '0').take(3)
        }

        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        )

        var result = value

        for (pattern in patterns) {

            try {
                val parser = SimpleDateFormat(
                    pattern,
                    Locale.US
                )

                parser.isLenient = false

                val date = parser.parse(normalized)

                if (date != null) {

                    result = SimpleDateFormat(
                        "dd MMM yyyy, HH:mm",
                        Locale.ENGLISH
                    ).format(date)

                    break
                }

            } catch (_: Exception) {
            }
        }

        result

    } catch (_: Exception) {
        value
    }
}
