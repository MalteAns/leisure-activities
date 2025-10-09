package de.malteans.sosactivities.core.presentation.util

enum class Plattform {
    ANDROID,
    IOS,
    DESKTOP,
    ;

    val isMobile: Boolean
        get() = this == ANDROID || this == IOS
    val isDesktop: Boolean
        get() = this == DESKTOP
}

expect fun currentPlattform(): Plattform