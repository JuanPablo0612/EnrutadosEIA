package com.juanpablo0612.carpool.presentation.rating

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.domain.rating.model.RatingError
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

fun Throwable.toRatingError(): RatingError = when (this) {
    is AppException.RatingException.Unknown -> RatingError.Unknown
    else -> RatingError.Unknown
}

fun RatingError.asStringResource(): StringResource = when (this) {
    RatingError.Unknown -> Res.string.error_unknown
}
