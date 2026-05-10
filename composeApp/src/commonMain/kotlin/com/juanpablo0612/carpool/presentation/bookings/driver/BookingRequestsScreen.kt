package com.juanpablo0612.carpool.presentation.bookings.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.juanpablo0612.carpool.domain.booking.model.Booking
import com.juanpablo0612.carpool.domain.booking.model.BookingStatus
import com.juanpablo0612.carpool.presentation.bookings.asStringResource
import com.juanpablo0612.carpool.presentation.bookings.driver.components.BookingRequestCard
import com.juanpablo0612.carpool.presentation.ui.components.EmptyState
import com.juanpablo0612.carpool.presentation.ui.components.ListSkeleton
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import com.juanpablo0612.carpool.presentation.ui.theme.Spacing
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.booking_requests_title
import enrutadoseia.composeapp.generated.resources.inbox_24px
import enrutadoseia.composeapp.generated.resources.requests_empty_subtitle
import enrutadoseia.composeapp.generated.resources.requests_empty_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BookingRequestsScreen(
    viewModel: BookingRequestsViewModel
) {
    val state by viewModel.state.collectAsState()

    BookingRequestsContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRequestsContent(
    state: BookingRequestsUiState,
    onAction: (BookingRequestsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.booking_requests_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (state.error != null) {
                Text(
                    text = stringResource(state.error.asStringResource()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
                )
            }
            when {
                state.isLoading -> ListSkeleton(modifier = Modifier.fillMaxSize())
                state.requests.isEmpty() -> EmptyState(
                    icon = vectorResource(Res.drawable.inbox_24px),
                    title = stringResource(Res.string.requests_empty_title),
                    description = stringResource(Res.string.requests_empty_subtitle),
                    modifier = Modifier.fillMaxSize()
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(state.requests, key = { it.id }) { booking ->
                            BookingRequestCard(
                                booking = booking,
                                processingIds = state.processingIds,
                                onConfirm = { onAction(BookingRequestsAction.OnConfirmClick(it)) },
                                onReject = { onAction(BookingRequestsAction.OnRejectClick(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BookingRequestsEmptyPreview() {
    CarpoolTheme {
        BookingRequestsContent(
            state = BookingRequestsUiState(isLoading = false, requests = emptyList()),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun BookingRequestsWithDataPreview() {
    CarpoolTheme {
        BookingRequestsContent(
            state = BookingRequestsUiState(
                isLoading = false,
                requests = listOf(
                    Booking(
                        id = "b1", tripId = "t1", passengerId = "p1", driverId = "d1",
                        passengerName = "María García", passengerEmail = "maria@eia.edu.co",
                        originName = "Casa", destinationName = "Universidad EIA",
                        departureTime = 1746360000000L, status = BookingStatus.Pending,
                        createdAt = 1746300000000L
                    )
                )
            ),
            onAction = {}
        )
    }
}
