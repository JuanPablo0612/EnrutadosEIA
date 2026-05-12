package com.juanpablo0612.carpool.presentation.vehicles.list

import com.juanpablo0612.carpool.domain.vehicles.model.Vehicle

sealed class VehiclesListAction {
    data class OnEditVehicle(val vehicleId: String) : VehiclesListAction()
    data class OnSetPrimary(val vehicleId: String) : VehiclesListAction()
    data class OnDeleteRequest(val vehicle: Vehicle) : VehiclesListAction()
    data object OnConfirmDelete : VehiclesListAction()
    data object OnDismissDeleteDialog : VehiclesListAction()
    data object OnDismissBlockedDialog : VehiclesListAction()
    data object OnAddVehicle : VehiclesListAction()
    data object OnBackClick : VehiclesListAction()
}
