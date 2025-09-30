package de.malteans.sosactivities

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform