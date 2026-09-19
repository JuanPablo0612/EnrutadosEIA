package com.juanpablo0612.carpool.presentation.route.community

sealed class CommunityRoutesEvent {
    data object NavigateBack : CommunityRoutesEvent()
}
