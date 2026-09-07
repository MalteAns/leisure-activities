package de.malteans.leisureactivities.security.token

data class TokenConfig(
    val issuer: String = "sos-activities",
    val audience: String = "sos-activities-audience",
    val expiresIn: Long = 365 * 24 * 60 * 60 * 1000L, // one year // TODO: Implement expiration handling
    val secret: String,
)