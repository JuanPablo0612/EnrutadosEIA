package com.juanpablo0612.carpool.domain.trip.validation

sealed class TripValidationError {
    data object OriginDestinationRequired : TripValidationError()
    data object SameOriginDestination : TripValidationError()
    data object DepartureTooSoon : TripValidationError()
    data object DepartureTooFar : TripValidationError()
    data object NoVehicleSelected : TripValidationError()
    data object SeatsOutOfRange : TripValidationError()
    data object ContributionOutOfRange : TripValidationError()
    data object MessageTooLong : TripValidationError()
    data object RouteNameRequired : TripValidationError()
}
