package de.malteans.leisureactivities.routes

import de.malteans.leisureactivities.model.CheckRegTokenResp
import de.malteans.leisureactivities.services.RegistrationTokensService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registrationTokensRoutes(
    registrationTokensService: RegistrationTokensService
) {
    get("/registration-tokens/{token}/check") {
        val token = call.parameters["token"]!!
        val valid = registrationTokensService.checkToken(token)
        call.respond(
            status = if (valid) HttpStatusCode.Accepted
                else HttpStatusCode(498, "Token expired/invalid"),
            message = CheckRegTokenResp(valid)
        )
    }
}