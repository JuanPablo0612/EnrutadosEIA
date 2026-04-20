package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    items: List<BottomNavItem<out Any>>,
    onNavigate: (route: Any) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = vectorResource(item.icon),
                        contentDescription = stringResource(item.label)
                    )
                },
                label = { Text(text = stringResource(item.label)) },
                selected = selected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
