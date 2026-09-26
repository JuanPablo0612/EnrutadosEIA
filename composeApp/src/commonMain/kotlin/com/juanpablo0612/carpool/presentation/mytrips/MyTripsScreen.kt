package com.juanpablo0612.carpool.presentation.mytrips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.presentation.mytrips.components.BookingRequestsEntry
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.my_trips_tab_driver
import enrutadoseia.composeapp.generated.resources.my_trips_tab_passenger
import enrutadoseia.composeapp.generated.resources.nav_my_trips
import org.jetbrains.compose.resources.stringResource

/**
 * "Mis viajes": the trips you ride and the trips you drive, as two tabs of one destination.
 * Each tab hosts an existing screen (with its own ViewModel) through a slot, so this screen only
 * owns the tab selection.
 */
@Composable
fun MyTripsScreen(
    initialTab: MyTripsTab,
    pendingRequestCount: Int,
    onOpenBookingRequests: () -> Unit,
    passengerContent: @Composable () -> Unit,
    driverContent: @Composable () -> Unit,
) {
    var selectedTab by rememberSaveable(initialTab) { mutableStateOf(initialTab) }
    MyTripsContent(
        selectedTab = selectedTab,
        pendingRequestCount = pendingRequestCount,
        onTabSelected = { selectedTab = it },
        onOpenBookingRequests = onOpenBookingRequests,
        passengerContent = passengerContent,
        driverContent = driverContent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsContent(
    selectedTab: MyTripsTab,
    pendingRequestCount: Int,
    onTabSelected: (MyTripsTab) -> Unit,
    onOpenBookingRequests: () -> Unit,
    passengerContent: @Composable () -> Unit,
    driverContent: @Composable () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.nav_my_trips)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
                Tab(
                    selected = selectedTab == MyTripsTab.Passenger,
                    onClick = { onTabSelected(MyTripsTab.Passenger) },
                    text = { Text(stringResource(Res.string.my_trips_tab_passenger), textAlign = TextAlign.Center) },
                )
                Tab(
                    selected = selectedTab == MyTripsTab.Driver,
                    onClick = { onTabSelected(MyTripsTab.Driver) },
                    text = {
                        BadgedBox(badge = {
                            if (pendingRequestCount > 0) Badge { Text(pendingRequestCount.toString()) }
                        }) {
                            Text(stringResource(Res.string.my_trips_tab_driver), textAlign = TextAlign.Center)
                        }
                    },
                )
            }
            when (selectedTab) {
                MyTripsTab.Passenger -> Box(modifier = Modifier.weight(1f)) { passengerContent() }
                MyTripsTab.Driver -> Column(modifier = Modifier.weight(1f)) {
                    BookingRequestsEntry(
                        pendingCount = pendingRequestCount,
                        onClick = onOpenBookingRequests,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    )
                    Box(modifier = Modifier.weight(1f)) { driverContent() }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MyTripsDriverTabPreview() {
    CarpoolTheme {
        MyTripsContent(
            selectedTab = MyTripsTab.Driver,
            pendingRequestCount = 2,
            onTabSelected = {},
            onOpenBookingRequests = {},
            passengerContent = {},
            driverContent = {},
        )
    }
}
