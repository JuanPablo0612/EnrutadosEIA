package com.juanpablo0612.carpool.presentation.profile.edit

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.edit_profile_error_bio_too_long
import enrutadoseia.composeapp.generated.resources.edit_profile_error_name_empty
import org.jetbrains.compose.resources.StringResource

sealed class EditProfileFieldError {
    data object NameEmpty : EditProfileFieldError()
    data object BioTooLong : EditProfileFieldError()

    fun asStringResource(): StringResource = when (this) {
        NameEmpty -> Res.string.edit_profile_error_name_empty
        BioTooLong -> Res.string.edit_profile_error_bio_too_long
    }
}
