package com.juanpablo0612.carpool.data.safety.model

import com.juanpablo0612.carpool.domain.safety.model.EmergencyContact
import kotlinx.serialization.Serializable

@Serializable
data class EmergencyContactDto(
    val id: String = "",
    val name: String = "",
    val phone: String = ""
) {
    fun toDomain(): EmergencyContact = EmergencyContact(id = id, name = name, phone = phone)

    companion object {
        fun fromDomain(contact: EmergencyContact): EmergencyContactDto =
            EmergencyContactDto(id = contact.id, name = contact.name, phone = contact.phone)
    }
}
