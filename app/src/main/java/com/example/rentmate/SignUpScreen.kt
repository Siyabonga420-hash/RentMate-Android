package com.example.rentmate

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

// =====================================================
// RENTMATE COLORS
// =====================================================

private val SignUpOrange = Color(0xFFF56038)
private val SignUpTextMain = Color(0xFF2D3142)
private val SignUpTextSub = Color(0xFF8D99AE)
private val SignUpBackground = Color(0xFFF4F5F7)
private val SignUpGreen = Color(0xFF168A4A)


// =====================================================
// MAIN SIGN UP SCREEN
// =====================================================

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    // Backend repository
    val repository = remember {
        RentMateRepository()
    }

    val scope = rememberCoroutineScope()

    // Registration states
    var isRegistering by remember {
        mutableStateOf(false)
    }

    var registrationError by remember {
        mutableStateOf("")
    }

    var showErrorDialog by remember {
        mutableStateOf(false)
    }

    // Personal information
    var fullName by remember {
        mutableStateOf("")
    }

    var idNumber by remember {
        mutableStateOf("")
    }

    var phoneNumber by remember {
        mutableStateOf("")
    }

    var accountType by remember {
        mutableStateOf("University Student")
    }

    // Academic information
    var studentNumber by remember {
        mutableStateOf("")
    }

    // Emergency contact
    var emergencyName by remember {
        mutableStateOf("")
    }

    var emergencyPhone by remember {
        mutableStateOf("")
    }

    // Login credentials
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var agreedToTerms by remember {
        mutableStateOf(false)
    }

    // Document selection
    var proofOfRegistration by remember {
        mutableStateOf<Uri?>(null)
    }

    var idDocument by remember {
        mutableStateOf<Uri?>(null)
    }

    // =====================================================
    // DOCUMENT PICKERS
    // =====================================================

    val proofPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        proofOfRegistration = uri
    }

    val idPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        idDocument = uri
    }

    // =====================================================
    // ERROR POPUP
    // =====================================================

    if (showErrorDialog) {

        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
            },

            title = {
                Text("Registration Error")
            },

            text = {
                Text(registrationError)
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showErrorDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // =====================================================
    // MAIN PAGE
    // =====================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SignUpBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "RentMate",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = SignUpOrange
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Create your account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SignUpTextMain
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Find your perfect housemate and make renting easier.",
            fontSize = 14.sp,
            color = SignUpTextSub
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // =====================================================
        // PERSONAL DETAILS
        // =====================================================

        SignUpSection(
            title = "Personal Details"
        ) {

            SignUpTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                },
                label = "Full Names",
                placeholder = "Enter your full name"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            SignUpTextField(
                value = idNumber,
                onValueChange = {

                    if (
                        it.length <= 13 &&
                        it.all(Char::isDigit)
                    ) {
                        idNumber = it
                    }
                },
                label = "South African ID Number",
                placeholder = "13 digit ID number"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            SignUpTextField(
                value = phoneNumber,
                onValueChange = {

                    if (
                        it.length <= 10 &&
                        it.all(Char::isDigit)
                    ) {
                        phoneNumber = it
                    }
                },
                label = "Phone Number",
                placeholder = "e.g. 0821234567"
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text = "Account Type",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SignUpTextMain
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = accountType == "University Student",
                    onClick = {
                        accountType = "University Student"
                    }
                )

                Text(
                    text = "University Student",
                    color = SignUpTextMain
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = accountType == "Non-Student Housemate",
                    onClick = {
                        accountType = "Non-Student Housemate"
                    }
                )

                Text(
                    text = "Non-Student Housemate",
                    color = SignUpTextMain
                )
            }
        }

        // =====================================================
        // ACADEMIC DETAILS
        // =====================================================

        if (accountType == "University Student") {

            SignUpSection(
                title = "Academic Details"
            ) {

                SignUpTextField(
                    value = studentNumber,
                    onValueChange = {

                        if (
                            it.length <= 9 &&
                            it.all(Char::isDigit)
                        ) {
                            studentNumber = it
                        }
                    },
                    label = "Student Number",
                    placeholder = "9 digit student number"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                SignUpTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = "Official Student Email",
                    placeholder = "Enter your student email"
                )
            }

        } else {

            SignUpSection(
                title = "Account Details"
            ) {

                SignUpTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = "Personal Email",
                    placeholder = "Enter your email"
                )
            }
        }

        // =====================================================
        // EMERGENCY CONTACT
        // =====================================================

        SignUpSection(
            title = "Emergency Contact"
        ) {

            SignUpTextField(
                value = emergencyName,
                onValueChange = {
                    emergencyName = it
                },
                label = "Emergency Contact Name",
                placeholder = "Full name"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            SignUpTextField(
                value = emergencyPhone,
                onValueChange = {

                    if (
                        it.length <= 10 &&
                        it.all(Char::isDigit)
                    ) {
                        emergencyPhone = it
                    }
                },
                label = "Emergency Contact Phone",
                placeholder = "e.g. 0821234567"
            )
        }

        // =====================================================
        // ACCOUNT CREDENTIALS
        // =====================================================

        SignUpSection(
            title = "Account Credentials"
        ) {

            SignUpTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = "Password",
                placeholder = "Create a strong password",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = {
                    passwordVisible = !passwordVisible
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Password must contain:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SignUpTextMain
            )

            PasswordRule(
                text = "At least 8 characters",
                valid = password.length >= 8
            )

            PasswordRule(
                text = "Uppercase letter",
                valid = password.any {
                    it.isUpperCase()
                }
            )

            PasswordRule(
                text = "Lowercase letter",
                valid = password.any {
                    it.isLowerCase()
                }
            )

            PasswordRule(
                text = "Number",
                valid = password.any {
                    it.isDigit()
                }
            )

            PasswordRule(
                text = "Special character",
                valid = password.any {
                    "@$!%*?&".contains(it)
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            SignUpTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                },
                label = "Confirm Password",
                placeholder = "Re-enter your password",
                isPassword = true
            )

            if (
                confirmPassword.isNotEmpty() &&
                password != confirmPassword
            ) {

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Passwords do not match",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        // =====================================================
        // VERIFICATION DOCUMENTS
        // =====================================================

        if (accountType == "University Student") {

            SignUpSection(
                title = "Verification Uploads"
            ) {

                Text(
                    text = "Proof of Registration (PDF / JPG)",
                    color = SignUpTextMain,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                DocumentUploadBox(
                    title = "Upload Proof of Registration",
                    subtitle = "Official current academic year document",
                    fileName = getFileName(proofOfRegistration),
                    onClick = {

                        proofPicker.launch(
                            arrayOf(
                                "application/pdf",
                                "image/*"
                            )
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = "ID Document / Smart Card Copy",
                    color = SignUpTextMain,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                DocumentUploadBox(
                    title = "Upload Certified ID / Passport",
                    subtitle = "Clear scan or photograph",
                    fileName = getFileName(idDocument),
                    onClick = {

                        idPicker.launch(
                            arrayOf(
                                "application/pdf",
                                "image/*"
                            )
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Documents are selected on this device but are not yet uploaded.",
                    color = SignUpTextSub,
                    fontSize = 12.sp
                )
            }
        }

        // =====================================================
        // TERMS AND CONDITIONS
        // =====================================================

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = agreedToTerms,
                onCheckedChange = {
                    agreedToTerms = it
                }
            )

            Text(
                text = "I agree to the Terms & Conditions",
                color = SignUpTextMain,
                fontSize = 13.sp
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // CREATE ACCOUNT BUTTON
        // =====================================================

        Button(
            onClick = {

                val validationError = registrationValidationError(
                    fullName = fullName,
                    idNumber = idNumber,
                    phoneNumber = phoneNumber,
                    accountType = accountType,
                    studentNumber = studentNumber,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    emergencyName = emergencyName,
                    emergencyPhone = emergencyPhone,
                    agreedToTerms = agreedToTerms,
                    proofOfRegistration = proofOfRegistration,
                    idDocument = idDocument
                )

                if (validationError != null) {

                    registrationError = validationError
                    showErrorDialog = true

                } else {

                    scope.launch {

                        isRegistering = true
                        registrationError = ""

                        try {

                            // =====================================
                            // SEND REGISTRATION TO BACKEND
                            // =====================================

                            repository.signUp(
                                email = email.trim(),
                                password = password,
                                fullName = fullName.trim(),
                                phone = phoneNumber.trim()
                            )

                            // Registration succeeded.
                            // Navigate to the login screen.

                            onSignUpSuccess()

                        } catch (e: Exception) {

                            registrationError = when (e) {

                                is SocketTimeoutException ->
                                    "The server took too long to respond. Your account may already exist. Try logging in before registering again."

                                else ->
                                    e.message
                                        ?: "Registration failed. Please try again."
                            }

                            showErrorDialog = true

                        } finally {

                            isRegistering = false
                        }
                    }
                }
            },

            enabled = !isRegistering,

            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = SignUpOrange
            ),

            shape = RoundedCornerShape(14.dp)
        ) {

            if (isRegistering) {

                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "Creating Account...",
                    color = Color.White
                )

            } else {

                Text(
                    text = "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // LOGIN LINK
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Already registered?",
                color = SignUpTextSub,
                fontSize = 13.sp
            )

            TextButton(
                onClick = onLoginClick
            ) {

                Text(
                    text = "Log In",
                    color = SignUpOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}


// =====================================================
// SECTION COMPONENT
// =====================================================

@Composable
fun SignUpSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SignUpTextMain
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        HorizontalDivider()

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        content()
    }
}


// =====================================================
// TEXT FIELD COMPONENT
// =====================================================

@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            color = SignUpTextMain,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),

            placeholder = {

                Text(
                    text = placeholder,
                    color = SignUpTextSub
                )
            },

            singleLine = true,

            visualTransformation =
                if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },

            trailingIcon = {

                if (
                    isPassword &&
                    onPasswordVisibilityChange != null
                ) {

                    TextButton(
                        onClick = onPasswordVisibilityChange
                    ) {

                        Text(
                            text = if (passwordVisible) {
                                "Hide"
                            } else {
                                "Show"
                            },
                            color = SignUpOrange,
                            fontSize = 11.sp
                        )
                    }
                }
            },

            shape = RoundedCornerShape(12.dp)
        )
    }
}


// =====================================================
// PASSWORD REQUIREMENT COMPONENT
// =====================================================

@Composable
fun PasswordRule(
    text: String,
    valid: Boolean
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = if (valid) "✓" else "○",
            color = if (valid) {
                SignUpGreen
            } else {
                SignUpTextSub
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.width(6.dp)
        )

        Text(
            text = text,
            color = SignUpTextSub,
            fontSize = 11.sp
        )
    }
}


// =====================================================
// DOCUMENT UPLOAD COMPONENT
// =====================================================

@Composable
fun DocumentUploadBox(
    title: String,
    subtitle: String,
    fileName: String?,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = SignUpOrange
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(15.dp)
    ) {

        Column {

            Text(
                text = "📄  $title",
                color = SignUpOrange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = subtitle,
                color = SignUpTextSub,
                fontSize = 11.sp
            )

            if (!fileName.isNullOrEmpty()) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Selected: $fileName",
                    color = SignUpGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// =====================================================
// DOCUMENT FILE NAME
// =====================================================

fun getFileName(uri: Uri?): String? {

    return uri?.lastPathSegment
}


// =====================================================
// SOUTH AFRICAN ID VALIDATION
// =====================================================

fun validateSouthAfricanId(
    id: String
): Boolean {

    return id.length == 13 &&
            id.all {
                it.isDigit()
            }
}


// =====================================================
// REGISTRATION VALIDATION
// =====================================================

fun registrationValidationError(
    fullName: String,
    idNumber: String,
    phoneNumber: String,
    accountType: String,
    studentNumber: String,
    email: String,
    password: String,
    confirmPassword: String,
    emergencyName: String,
    emergencyPhone: String,
    agreedToTerms: Boolean,
    proofOfRegistration: Uri?,
    idDocument: Uri?
): String? {

    // Full name
    if (fullName.isBlank()) {
        return "Enter your full name."
    }

    // South African ID
    if (!validateSouthAfricanId(idNumber)) {
        return "Enter a 13-digit South African ID number."
    }

    // Phone number
    if (
        phoneNumber.length != 10 ||
        !phoneNumber.startsWith("0") ||
        !phoneNumber.all(Char::isDigit)
    ) {
        return "Enter a valid 10-digit phone number starting with 0."
    }

    // Emergency contact name
    if (emergencyName.isBlank()) {
        return "Enter an emergency contact name."
    }

    // Emergency contact phone
    if (
        emergencyPhone.length != 10 ||
        !emergencyPhone.startsWith("0") ||
        !emergencyPhone.all(Char::isDigit)
    ) {
        return "Enter a valid emergency contact number."
    }

    // Email address
    if (
        !Patterns.EMAIL_ADDRESS
            .matcher(email.trim())
            .matches()
    ) {
        return "Enter a valid email address."
    }

    // Student number
    if (accountType == "University Student") {

        if (
            studentNumber.length != 9 ||
            !studentNumber.all(Char::isDigit)
        ) {
            return "Enter your 9-digit student number."
        }
    }

    // Password length
    if (password.length < 8) {
        return "Password must contain at least 8 characters."
    }

    // Uppercase
    if (!password.any(Char::isUpperCase)) {
        return "Password must contain an uppercase letter."
    }

    // Lowercase
    if (!password.any(Char::isLowerCase)) {
        return "Password must contain a lowercase letter."
    }

    // Number
    if (!password.any(Char::isDigit)) {
        return "Password must contain a number."
    }

    // Special character
    if (
        !password.any {
            "@$!%*?&".contains(it)
        }
    ) {
        return "Password must contain a special character."
    }

    // Confirm password
    if (password != confirmPassword) {
        return "Passwords do not match."
    }

    // Student documents
    if (accountType == "University Student") {

        if (proofOfRegistration == null) {
            return "Select your proof of registration."
        }

        if (idDocument == null) {
            return "Select your ID document."
        }
    }

    // Terms and conditions
    if (!agreedToTerms) {
        return "Agree to the Terms & Conditions to continue."
    }

    return null
}


// =====================================================
// COMPATIBILITY FUNCTION
// =====================================================

fun validateAndRegister(
    fullName: String,
    idNumber: String,
    phoneNumber: String,
    accountType: String,
    studentNumber: String,
    email: String,
    password: String,
    confirmPassword: String,
    emergencyName: String,
    emergencyPhone: String,
    agreedToTerms: Boolean,
    proofOfRegistration: Uri?,
    idDocument: Uri?
): Boolean {

    return registrationValidationError(
        fullName = fullName,
        idNumber = idNumber,
        phoneNumber = phoneNumber,
        accountType = accountType,
        studentNumber = studentNumber,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        emergencyName = emergencyName,
        emergencyPhone = emergencyPhone,
        agreedToTerms = agreedToTerms,
        proofOfRegistration = proofOfRegistration,
        idDocument = idDocument
    ) == null
}


// =====================================================
// PREVIEW
// =====================================================

@Preview(
    name = "RentMate Sign Up",
    showBackground = true,
    showSystemUi = false,
    widthDp = 410,
    heightDp = 840
)
@Composable
fun SignUpScreenPreview() {

    MaterialTheme {

        SignUpScreen()
    }
}