package com.juanpablo0612.carpool.presentation.places.selector

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.places.model.Place
import com.juanpablo0612.carpool.presentation.places.selector.components.AddPlaceDialog
import com.juanpablo0612.carpool.presentation.places.selector.components.PlaceItem
import enrutadoseia.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSelectorScreen(
    viewModel: PlaceSelectorViewModel,
    onPlaceSelected: (Place) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.search_places)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.arrow_back_24px),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.add_24px),
                            contentDescription = "Add Place"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onAction(PlaceSelectorAction.OnQueryChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.search_places)) },
                leadingIcon = { 
                    Icon(
                        imageVector = vectorResource(Res.drawable.search_24px),
                        contentDescription = null
                    ) 
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val displayList = if (state.query.isBlank()) state.savedPlaces else state.searchResults
                if (displayList.isEmpty() && !state.isLoading) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (state.query.isBlank()) stringResource(Res.string.no_saved_places) else stringResource(Res.string.no_results_found),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                items(displayList, key = { it.id }) { place ->
                    PlaceItem(
                        place = place,
                        onClick = { onPlaceSelected(place) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showAddDialog) {
        AddPlaceDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, address ->
                viewModel.onAction(PlaceSelectorAction.OnSavePlace(name, address, 0.0, 0.0))
                showAddDialog = false
            }
        )
    }
}
