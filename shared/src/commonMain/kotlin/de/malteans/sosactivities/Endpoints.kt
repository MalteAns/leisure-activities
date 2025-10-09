package de.malteans.sosactivities

sealed class Endpoints(
    val url: String,
) {
    data object CreateUser: Endpoints(
        "${Constants.BASE_URL}/v1/auth/register",
    )
    data class RegTokenCheck(val token: String) : Endpoints(
        "${Constants.BASE_URL}/v1/registration-tokens/$token/check",
    )
    data object JwkTokenCheck: Endpoints(
        "${Constants.BASE_URL}/v1/auth/token/check",
    )
    data object JwkTokenRefresh: Endpoints(
        "${Constants.BASE_URL}/v1/auth/token/refresh",
    )
    data object UserInfo: Endpoints(
        "${Constants.BASE_URL}/v1/me",
    )
    data object UserUpdate: Endpoints(
        "${Constants.BASE_URL}/v1/me",
    )
    data object UserSignUps: Endpoints(
        "${Constants.BASE_URL}/v1/me/signups",
    )
    data object AllActivities: Endpoints(
        "${Constants.BASE_URL}/v1/activities",
    )
    data class Activity(val activityId: String): Endpoints(
        "${Constants.BASE_URL}/v1/activities/$activityId",
    )
    data class ActivitySignUp(val activityId: String): Endpoints(
        "${Constants.BASE_URL}/v1/activities/$activityId/signup",
    )
    data object ImagePresign: Endpoints(
        "${Constants.BASE_URL}/v1/images/presign",
    )
    data class ImageUpload(val imageId: String): Endpoints(
        "${Constants.BASE_URL}/v1/images/$imageId/upload",
    )
    data class ImageFinalize(val imageId: String): Endpoints(
        "${Constants.BASE_URL}/v1/images/$imageId/finalize",
    )
    data object Images: Endpoints(
        "${Constants.BASE_URL}/v1/images",
    )
}