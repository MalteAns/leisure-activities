package de.malteans.leisureactivities.registration.presentation

import de.malteans.leisureactivities.model.User

data class RegistrationState(
    val isLoading: Boolean = false,
    val registrationToken: String = "uidt06AkZYlB1UrwRcEqd1lHjgQa4spqXefd21vci5RyQ0T0SQKK0phzzXYvmWfA14bUfIBT98vfnq6Kr9mBo5pE23p4WswSkSKmveZ2FVEx6B3ZArrQxrc0CrL1ABM0", // FIXME: Remove test token
    val validToken: Boolean? = false,
    val firstName: String = "",
    val lastName: String = "",
    val createdUser: User? = null,
    val error: Throwable? = null,
)
