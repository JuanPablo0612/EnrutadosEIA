package com.juanpablo0612.carpool.presentation.routes.search

data class SearchFilters(
    val maxContribution: Int? = null,
    val verifiedOnly: Boolean = false
)
