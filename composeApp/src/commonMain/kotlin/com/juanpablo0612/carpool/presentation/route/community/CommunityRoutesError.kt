package com.juanpablo0612.carpool.presentation.route.community

import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.error_route_reuse_failed
import enrutadoseia.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

sealed class CommunityRoutesError {
    data object LoadFailed : CommunityRoutesError()
    data object ReuseFailed : CommunityRoutesError()

    fun asStringResource(): StringResource = when (this) {
        LoadFailed -> Res.string.error_unknown
        ReuseFailed -> Res.string.error_route_reuse_failed
    }
}
