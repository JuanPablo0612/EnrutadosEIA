package com.juanpablo0612.carpool

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.juanpablo0612.carpool.presentation.navigation.AppNavigation
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme

@Composable
fun App() {
    CarpoolTheme {
        val navController = rememberNavController()
        AppNavigation(navController = navController)
    }
}
