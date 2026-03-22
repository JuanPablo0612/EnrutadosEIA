package com.juanpablo0612.carpool

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.juanpablo0612.carpool.presentation.navigation.AppNavigation
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme

@Composable
fun App() {
    CarpoolTheme {
        val navController = rememberNavController()

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
