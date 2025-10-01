package de.malteans.sosactivities.models.ext

import de.malteans.sosactivities.UserDto
import de.malteans.sosactivities.models.User
import de.malteans.sosactivities.security.Role

val User.displayName: String
    get() = "$firstName $lastName"

val User.roles: Set<Role>
    get() = mutableSetOf<Role>()
        .let { set ->
            if (isAdmin) set.add(Role.ADMIN)
            if (isStaff) set.add(Role.STAFF)
            set
        }

fun User.toDto(
    id: String = this.id,
    name: String = this.displayName,
    isStaff: Boolean = this.isStaff,
    isAdmin: Boolean = this.isAdmin,
) = UserDto(
    id = id,
    name = name,
    isStaff = isStaff,
    isAdmin = isAdmin,
)