package de.malteans.sosactivities.plugins

import de.malteans.sosactivities.CreateActivityReq
import de.malteans.sosactivities.CreateUserReq
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<CreateUserReq> {
            when {
                it.id.isBlank() -> ValidationResult.Invalid("id is blank")
                it.firstName.isBlank() -> ValidationResult.Invalid("firstName is blank")
                it.lastName.isBlank() -> ValidationResult.Invalid("lastName is blank")
                else -> ValidationResult.Valid
            }
        }
        validate<CreateActivityReq> {
            when {
                it.title.isBlank() -> ValidationResult.Invalid("title is blank")
                it.durationMin !in 1..(12*60) -> ValidationResult.Invalid("durationMin out of range")
                else -> ValidationResult.Valid
            }
        }
    }
}