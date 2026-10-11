package com.example.rentmate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun ProfileScreen(onBackClick: () -> Unit, onLogoutClick: () -> Unit) {
    val repo = remember { RentMateRepository() }
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf<JSONObject?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    LaunchedEffect(RentMateSession.userId) {
        try {
            profile = repo.getProfile()
            name = profile?.optString("full_name").orEmpty()
            phone = profile?.optString("phone").orEmpty()
        } catch (e: Exception) { message = e.message ?: "Unable to load profile" }
    }
    Column(Modifier.fillMaxSize().background(Color(0xFFF4F5F7)).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("My Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = CircleShape, color = Color(0xFFF56038), modifier = Modifier.size(80.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(name.firstOrNull()?.uppercase() ?: "R", color = Color.White, style = MaterialTheme.typography.headlineLarge) }
                }
                Spacer(Modifier.height(10.dp))
                Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(profile?.optString("email").orEmpty())
                Spacer(Modifier.height(8.dp))
                Text("Account: ${profile?.optString("role", "tenant") ?: "tenant"}")
                Text("Verification: Not verified / awaiting review")
            }
        }
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(18.dp)) {
                Text("PERSONAL DETAILS", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(name, { name = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(phone, { phone = it }, label = { Text("SA cellphone number") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Button(enabled = !busy, onClick = { scope.launch {
                    busy = true
                    try { profile = repo.updateProfile(name, phone); message = "Profile saved" }
                    catch (e: Exception) { message = e.message ?: "Unable to save" }
                    finally { busy = false }
                } }) { Text("Save details") }
            }
        }
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(18.dp)) {
                Text("SECURITY & PASSWORD", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(currentPassword, { currentPassword = it }, label = { Text("Current password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(newPassword, { newPassword = it }, label = { Text("New password (10+ characters)") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Button(enabled = !busy, onClick = { scope.launch {
                    busy = true
                    try { repo.changePassword(currentPassword, newPassword); currentPassword = ""; newPassword = ""; message = "Password updated" }
                    catch (e: Exception) { message = e.message ?: "Unable to change password" }
                    finally { busy = false }
                } }) { Text("Change password") }
            }
        }
        if (message.isNotBlank()) { Spacer(Modifier.height(12.dp)); Text(message) }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBackClick) { Text("Back to dashboard") }
        TextButton(onClick = onLogoutClick) { Text("Log out") }
    }
}
