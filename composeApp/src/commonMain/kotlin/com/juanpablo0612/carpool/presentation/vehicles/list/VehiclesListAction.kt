package com.juanpablo0612.carpool.presentation.vehicles.list

sealed class VehiclesListAction {
    data object OnRegisterVehicleClick : VehiclesListAction()
}
