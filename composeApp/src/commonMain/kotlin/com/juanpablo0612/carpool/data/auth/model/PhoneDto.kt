package com.juanpablo0612.carpool.data.auth.model

import com.juanpablo0612.carpool.domain.auth.model.PhoneNumber

/**
 * The two parts of a phone number on their way to the data source, which writes them as the
 * flat `phoneCountryCode` and `phoneNumber` fields of the user document (see [UserDto]).
 */
data class PhoneDto(
    val countryCode: String = "",
    val number: String = "",
)

fun PhoneNumber.toDto(): PhoneDto = PhoneDto(countryCode = countryCode, number = number)
