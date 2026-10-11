package com.example.rentmate

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val navy = Color(0xFF2D3142)
private val green = Color(0xFF00813D)

@Composable
fun RentMateBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {

    val items = listOf(
        Triple("dashboard", "Home", Icons.Filled.Home),
        Triple("accommodation_details", "Explore", Icons.Filled.Search),
        Triple("activity", "Activity", Icons.Filled.Notifications),
        Triple("profile", "Profile", Icons.Filled.Person)
    )

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White
    ) {

        items.forEach { (route, label, icon) ->

            NavigationBarItem(
                selected = currentRoute == route,

                onClick = {
                    if (currentRoute != route) {
                        onNavigate(route)
                    }
                },

                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },

                label = {
                    Text(label)
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = green,
                    selectedTextColor = green,
                    indicatorColor = Color(0xFFE5F5EC),
                    unselectedIconColor = navy,
                    unselectedTextColor = navy
                )
            )
        }
    }
}

