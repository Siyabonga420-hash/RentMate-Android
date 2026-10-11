package com.example.rentmate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LoginBackground = Color(0xFFF4F5F7)
private val LoginOrange = Color(0xFFF56038)
private val LoginTextMain = Color(0xFF2D3142)
private val LoginTextSub = Color(0xFF8D99AE)
private val LoginSuccess = Color(0xFF10B981)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {

    val loginScope = rememberCoroutineScope()
    var loginBusy by remember { mutableStateOf(false) }
    var loginMessage by remember { mutableStateOf("") }
    var invalidCredentials by remember { mutableStateOf(false) }

    if (invalidCredentials) {
        AlertDialog(
            onDismissRequest = { invalidCredentials = false },
            title = { Text("Sign in failed") },
            text = { Text("Invalid email or password. Please try again.") },
            confirmButton = { TextButton(onClick = { invalidCredentials = false }) { Text("OK") } }
        )
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var emailError by remember {
        mutableStateOf(false)
    }

    var passwordError by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ------------------------------------------------
        // BRAND ICON
        // ------------------------------------------------

        Text(
            text = "🏠",
            fontSize = 38.sp,
            modifier = Modifier
                .background(
                    color = LoginOrange,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ------------------------------------------------
        // HEADER
        // ------------------------------------------------

        Text(
            text = "Welcome Back",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LoginTextMain
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Log in to manage rent, bills, and chores",
            fontSize = 12.sp,
            color = LoginTextSub,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ------------------------------------------------
        // FORM
        // ------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {

            // EMAIL

            Text(
                text = "Email Address",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LoginTextMain
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "student@myuct.ac.za",
                        fontSize = 12.sp,
                        color = LoginTextSub
                    )
                },
                isError = emailError,
                shape = RoundedCornerShape(10.dp)
            )

            if (emailError) {

                Text(
                    text = "Please enter a valid email address",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // PASSWORD

            Text(
                text = "Password",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LoginTextMain
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "••••••••",
                        fontSize = 12.sp,
                        color = LoginTextSub
                    )
                },
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                trailingIcon = {

                    TextButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {

                        Text(
                            text = if (passwordVisible) {
                                "Hide"
                            } else {
                                "Show"
                            },
                            color = LoginOrange,
                            fontSize = 11.sp
                        )
                    }
                },
                isError = passwordError,
                shape = RoundedCornerShape(10.dp)
            )

            if (passwordError) {

                Text(
                    text = "Password is required",
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (loginMessage.isNotBlank()) {
                Text(loginMessage, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

                        // LOGIN BUTTON

            Button(
                onClick = {

                    emailError =
                        email.isBlank() ||
                                !android.util.Patterns.EMAIL_ADDRESS
                                    .matcher(email)
                                    .matches()

                    passwordError =
                        password.isBlank()

                    if (!emailError && !passwordError) {

                        if (!loginBusy) {
                            loginBusy = true
                            loginMessage = ""
                            loginScope.launch {
                                try {
                                    RentMateRepository().signIn(email.trim(), password)
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    if (e.message?.contains("401") == true || e.message?.contains("Invalid email or password", true) == true || e.message?.contains("Incorrect email or password", true) == true) { invalidCredentials = true } else { loginMessage = e.message ?: "Unable to sign in" }
                                } finally {
                                    loginBusy = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginOrange
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Text(
                    text = "Log In & Go to Dashboard",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ------------------------------------------------
        // SIGN UP
        // ------------------------------------------------

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Don't have an account?",
                color = LoginTextSub,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            TextButton(
                onClick = onSignUpClick
            ) {

                Text(
                    text = "Sign Up & Verify",
                    color = LoginOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ------------------------------------------------
        // SECURITY BADGE
        // ------------------------------------------------

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🛡",
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = "Student FICA & ID Verified Portal",
                color = LoginTextSub,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    name = "RentMate Login",
    showBackground = true,
    showSystemUi = false,
    widthDp = 410,
    heightDp = 840
)
@Composable
fun LoginScreenPreview() {

    LoginScreen()
}

