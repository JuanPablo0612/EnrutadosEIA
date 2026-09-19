package com.juanpablo0612.carpool.presentation.route.community

sealed class CommunityRoutesAction {
    data class OnReuseClick(val routeId: String) : CommunityRoutesAction()
    data object OnRetry : CommunityRoutesAction()
    data object OnDismissActionError : CommunityRoutesAction()
    data object OnBackClick : CommunityRoutesAction()
}
