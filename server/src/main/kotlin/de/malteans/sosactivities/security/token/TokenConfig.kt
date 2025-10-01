package de.malteans.sosactivities.security.token

data class TokenConfig(
    val issuer: String = "sos-activities",
    val audience: String = "sos-activities-audience",
    val expiresIn: Long = 365 * 24 * 60 * 60, // one year
    val secret: String = "secret" // TODO: use env variable or file
)