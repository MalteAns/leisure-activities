package de.malteans.sosactivities.core.data.network

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T> {
    val response = try {
        execute()
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.failure(e)
    }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                Result.success(response.body<T>())
            } catch(e: NoTransformationFoundException) {
                Result.failure(e)
            }
        }
        else -> Result.failure(HttpStatusException(response.status))
    }
}

data class HttpStatusException(val statusCode: HttpStatusCode) : Exception()
