package com.juanpablo0612.carpool.presentation.profile.edit

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.edit_profile_error_bio_too_long
import enrutadoseia.composeapp.generated.resources.edit_profile_error_name_empty
import enrutadoseia.composeapp.generated.resources.error_phone_invalid
import org.jetbrains.compose.resources.StringResource

sealed class EditProfileFieldError {
    data object NameEmpty : EditProfileFieldError()
    data object PhoneInvalid : EditProfileFieldError()
    data object BioTooLong : EditProfileFieldError()

    fun asStringResource(): StringResource = when (this) {
        NameEmpty -> Res.string.edit_profile_error_name_empty
        PhoneInvalid -> Res.string.error_phone_invalid
        BioTooLong -> Res.string.edit_profile_error_bio_too_long
    }
}
