package de.malteans.leisureactivities.core.presentation.util

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
    ;

    val isMobile: Boolean
        get() = this == ANDROID || this == IOS
    val isDesktop: Boolean
        get() = this == DESKTOP
}

expect fun currentPlatform(): Platform