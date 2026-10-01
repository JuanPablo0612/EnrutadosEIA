package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.juanpablo0612.carpool.presentation.ui.theme.Elevation
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * The app's single bottom bar. It carries no badges: each would need a live Firestore listener
 * for the whole session, and the screens themselves surface what needs attention.
 */
@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    items: List<BottomNavItem<out Any>>,
    onNavigate: (route: Any) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    // The bar sits on white like the cards, separated from content by a hairline instead of the
    // tonal elevation NavigationBar applies by default.
    Column {
        HorizontalDivider(color = colors.outlineVariant)
        NavigationBar(
            containerColor = colors.surfaceContainerLowest,
            tonalElevation = Elevation.none,
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                NavigationBarItem(
                    icon = {
                        // The label below already names the item; describing the icon too would
                        // make TalkBack read it twice.
                        Icon(imageVector = vectorResource(item.icon), contentDescription = null)
                    },
                    label = {
                        Text(
                            text = stringResource(item.label),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = selected,
                    alwaysShowLabel = true,
                    onClick = { onNavigate(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.onSecondaryContainer,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.secondaryContainer,
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}
