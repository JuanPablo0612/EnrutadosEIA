package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import kotlin.reflect.KClass

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    items: List<BottomNavItem<out Any>>,
    badgeCounts: Map<KClass<*>, Int> = emptyMap(),
    onNavigate: (route: Any) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            val count = badgeCounts[item.route::class] ?: 0
            NavigationBarItem(
                icon = {
                    BadgedBox(badge = {
                        if (count > 0) Badge { Text(count.toString()) }
                    }) {
                        Icon(
                            imageVector = vectorResource(item.icon),
                            contentDescription = stringResource(item.label)
                        )
                    }
                },
                label = { Text(text = stringResource(item.label)) },
                selected = selected,
                alwaysShowLabel = false,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
