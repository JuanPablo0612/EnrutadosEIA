package com.juanpablo0612.carpool.presentation.routes.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanpablo0612.carpool.domain.routes.model.Route
import com.juanpablo0612.carpool.domain.routes.model.RouteType
import com.juanpablo0612.carpool.domain.routes.use_case.GetAvailableRoutesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SearchRoutesViewModel(
    getAvailableRoutesUseCase: GetAvailableRoutesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchRoutesUiState())
    val uiState = _uiState.asStateFlow()

    private val allRoutes = MutableStateFlow<List<Route>>(emptyList())
    private val query = MutableStateFlow("")
    private val selectedType = MutableStateFlow<RouteType?>(null)

    init {
        getAvailableRoutesUseCase()
            .onEach { routes ->
                allRoutes.value = routes
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)

        combine(allRoutes, query, selectedType) { routes, q, type ->
            routes.filter { route ->
                val matchesQuery = q.isBlank() ||
                    route.origin.name.contains(q, ignoreCase = true) ||
                    route.destination.name.contains(q, ignoreCase = true)
                val matchesType = type == null || route.type == type
                matchesQuery && matchesType
            }
        }.onEach { filtered ->
            _uiState.update { it.copy(routes = filtered) }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: SearchRoutesAction) {
        when (action) {
            is SearchRoutesAction.OnQueryChanged -> {
                query.value = action.query
                _uiState.update { it.copy(query = action.query) }
            }
            is SearchRoutesAction.OnTypeFilterChanged -> {
                selectedType.value = action.type
                _uiState.update { it.copy(selectedType = action.type) }
            }
        }
    }
}
