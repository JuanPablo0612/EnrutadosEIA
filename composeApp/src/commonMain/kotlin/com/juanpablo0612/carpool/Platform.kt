package com.juanpablo0612.carpool

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform