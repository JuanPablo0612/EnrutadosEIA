package com.juanpablo0612.carpool.presentation.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.juanpablo0612.carpool.presentation.ui.util.ScreenInsets
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * The [BottomNavigationBar]'s counterpart on medium and expanded windows (unfolded foldables,
 * tablets): the same destinations and colours down the start edge, where a bar would stretch
 * four items across a wide screen.
 */
@Composable
fun NavigationRailBar(
    currentDestination: NavDestination?,
    items: List<BottomNavItem<out Any>>,
    onNavigate: (route: Any) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    // White like the bottom bar, separated from content by a hairline rather than elevation.
    Row {
        NavigationRail(
            containerColor = colors.surfaceContainerLowest,
            windowInsets = ScreenInsets.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                NavigationRailItem(
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
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = colors.onSecondaryContainer,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.secondaryContainer,
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant,
                    ),
                )
            }
        }
        VerticalDivider(color = colors.outlineVariant)
    }
}
