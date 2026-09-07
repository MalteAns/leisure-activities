package de.malteans.leisureactivities.model.ext

import de.malteans.leisureactivities.dto.UserDto
import de.malteans.leisureactivities.model.Role
import de.malteans.leisureactivities.model.User

val User.displayName: String
    get() = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "Unkown" }

val User.roles: Set<Role>
    get() = mutableSetOf<Role>()
        .let { set ->
            if (isAdmin) set.add(Role.ADMIN)
            if (isStaff) set.add(Role.STAFF)
            set
        }

fun User.toDto(
    id: String = this.id,
    firstName: String = this.firstName,
    lastName: String = this.lastName,
    isStaff: Boolean = this.isStaff,
    isAdmin: Boolean = this.isAdmin,
) = UserDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    isStaff = isStaff,
    isAdmin = isAdmin,
)

fun UserDto.toDomain(
    id: String = this.id,
    firstName: String = this.firstName,
    lastName: String = this.lastName,
    isStaff: Boolean = this.isStaff,
    isAdmin: Boolean = this.isAdmin,
) = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    isStaff = isStaff,
    isAdmin = isAdmin,
)