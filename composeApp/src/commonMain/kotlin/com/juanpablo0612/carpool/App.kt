package com.juanpablo0612.carpool

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.juanpablo0612.carpool.presentation.navigation.AppNavigation
import io.github.vinceglb.filekit.coil.addPlatformFileSupport

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                addPlatformFileSupport()
            }
            .build()
    }

    val navController = rememberNavController()
    AppNavigation(navController = navController)
}
