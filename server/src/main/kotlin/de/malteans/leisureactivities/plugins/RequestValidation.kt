package de.malteans.leisureactivities.plugins

import de.malteans.leisureactivities.dto.CreateActivityReq
import de.malteans.leisureactivities.dto.CreateUserReq
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<CreateUserReq> { req ->
            when {
                req.registrationToken.length != 128
                    || req.registrationToken.any { !it.isLetterOrDigit() }
                        -> ValidationResult.Invalid("token is wrong format")
                req.firstName.isBlank() -> ValidationResult.Invalid("firstName is blank")
                req.lastName.isBlank() -> ValidationResult.Invalid("lastName is blank")
                else -> ValidationResult.Valid
            }
        }
        validate<CreateActivityReq> {
            when {
                it.title.isBlank() -> ValidationResult.Invalid("title is blank")
                else -> ValidationResult.Valid
            }
        }
    }
}