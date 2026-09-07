package de.malteans.leisureactivities

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform