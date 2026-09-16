package com.tarikturkdil.photoProject.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.tarikturkdil.photoProject.ui.navigation.Screen

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Feed, Icons.Filled.Home, "Ana Sayfa"),
    BottomNavItem(Screen.Search, Icons.Filled.Search, "Ara"),
    BottomNavItem(Screen.AddPin, Icons.Filled.AddCircle, "Ekle"),
    BottomNavItem(Screen.Notifications, Icons.Filled.Favorite, "Bildirimler"),
    BottomNavItem(Screen.Profile, Icons.Filled.Person, "Profil")
)