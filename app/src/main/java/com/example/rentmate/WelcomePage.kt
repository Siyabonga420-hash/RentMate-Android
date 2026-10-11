
package com.example.rentmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


// Legacy join/create screens preserved; active welcome and signup live in their own files.

@Composable
fun JoinAMateScreen(
    onSignUpClick: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf("") }
    var moveInDate by remember { mutableStateOf("") }
    var chorePreference by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF5F5F5),
                        Color(0xFFD0D0D0)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 25.dp,
                    vertical = 25.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // logo
            Text(
                text = "RentMate",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(35.dp))


            Text(
                text = "Join A Mate",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(65.dp))

            // Name
            JoinMateTextField(
                label = "Name:",
                value = name,
                onValueChange = {
                    name = it
                }
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Surname
            JoinMateTextField(
                label = "Surname:",
                value = surname,
                onValueChange = {
                    surname = it
                }
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Budget
            JoinMateTextField(
                label = "Budget:",
                value = budget,
                onValueChange = {
                    budget = it
                }
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Contacts
            JoinMateTextField(
                label = "Contacts:",
                value = contacts,
                onValueChange = {
                    contacts = it
                }
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Move-in date
            JoinMateTextField(
                label = "Date (Move-In):",
                value = moveInDate,
                onValueChange = {
                    moveInDate = it
                }
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Chore preference
            JoinMateTextField(
                label = "Chore Preference:",
                value = chorePreference,
                onValueChange = {
                    chorePreference = it
                }
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Gender
            JoinMateTextField(
                label = "Gender:",
                value = gender,
                onValueChange = {
                    gender = it
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // About Me
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Text(
                    text = "About Me:",
                    fontSize = 10.sp,
                    modifier = Modifier
                        .width(75.dp)
                        .padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = aboutMe,
                    onValueChange = {
                        aboutMe = it
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .height(90.dp),
                    shape = RoundedCornerShape(7.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up button
            Button(
                onClick = onSignUpClick,
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8BC66A),
                    contentColor = Color.Black
                )
            ) {

                Text(
                    text = "Sign Up",
                    fontSize = 10.sp
                )
            }
        }
    }
}

// JOIN A MATE TEXT FIELD

@Composable
fun JoinMateTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            fontSize = 10.sp,
            modifier = Modifier.width(85.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = RoundedCornerShape(3.dp),
            singleLine = true
        )
    }
}

// PAGE 3 PREVIEW
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "RentMate Page 3 - Join A Mate"
)
@Composable
fun JoinAMateScreenPreview() {

    JoinAMateScreen(
        onSignUpClick = {}
    )
}

// PAGE 4 - CREATE A HOUSE


@Composable
fun CreateHouseScreen(
    onSignUpClick: () -> Unit
) {

    var houseName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var rent by remember { mutableStateOf("") }
    var roomDetails by remember { mutableStateOf("") }
    var houseRules by remember { mutableStateOf("") }
    var choreExpectations by remember { mutableStateOf("") }
    var houseAmenities by remember { mutableStateOf("") }
    var preferredRoommate by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF5F5F5),
                        Color(0xFFD0D0D0)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 25.dp,
                    vertical = 25.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // logo
            Text(
                text = "RentMate",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(35.dp))


            Text(
                text = "Create A House",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(35.dp))

            // House Name
            CreateHouseTextField(
                label = "House Name:",
                value = houseName,
                onValueChange = {
                    houseName = it
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Location
            CreateHouseTextField(
                label = "Location:",
                value = location,
                onValueChange = {
                    location = it
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Rent
            CreateHouseTextField(
                label = "Rent (Monthly):",
                value = rent,
                onValueChange = {
                    rent = it
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Room Details
            CreateHouseTextField(
                label = "Room Details:",
                value = roomDetails,
                onValueChange = {
                    roomDetails = it
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // House Rules
            CreateHouseTextField(
                label = "House Rules:",
                value = houseRules,
                onValueChange = {
                    houseRules = it
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Chore Expectations
            CreateHouseTextField(
                label = "Chore Expectations:",
                value = choreExpectations,
                onValueChange = {
                    choreExpectations = it
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // House Amenities
            CreateHouseTextField(
                label = "House Amenities:",
                value = houseAmenities,
                onValueChange = {
                    houseAmenities = it
                }
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Photos
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Photos:",
                    fontSize = 10.sp,
                    modifier = Modifier.width(85.dp)
                )

                PhotoButton()
                Spacer(modifier = Modifier.width(8.dp))
                PhotoButton()
                Spacer(modifier = Modifier.width(8.dp))
                PhotoButton()
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preferred Roommate
            CreateHouseTextField(
                label = "Preferred Roommate:",
                value = preferredRoommate,
                onValueChange = {
                    preferredRoommate = it
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Contacts
            CreateHouseTextField(
                label = "Contacts:",
                value = contacts,
                onValueChange = {
                    contacts = it
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up
            Button(
                onClick = onSignUpClick,
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8BC66A),
                    contentColor = Color.Black
                )
            ) {

                Text(
                    text = "Sign Up",
                    fontSize = 10.sp
                )
            }
        }
    }
}

// CREATE A HOUSE TEXT FIELD

@Composable
fun CreateHouseTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            fontSize = 9.sp,
            modifier = Modifier.width(85.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = RoundedCornerShape(3.dp),
            singleLine = true
        )
    }
}



// PHOTO BUTTON


@Composable
fun PhotoButton() {

    Button(
        onClick = {
            // Photo upload will be added later
        },
        modifier = Modifier
            .width(60.dp)
            .height(75.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {

        Text(
            text = "+",
            fontSize = 12.sp
        )
    }
}

// PAGE 4 PREVIEW

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "RentMate Page 4 - Create A House"
)
@Composable
fun CreateHouseScreenPreview() {

    CreateHouseScreen(
        onSignUpClick = {}
    )
}
