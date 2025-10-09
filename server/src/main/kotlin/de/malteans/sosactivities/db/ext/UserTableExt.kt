package de.malteans.sosactivities.db.ext

import de.malteans.sosactivities.db.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.concat
import org.jetbrains.exposed.sql.stringLiteral

val UsersTable.displayName
    get() = concat(
        UsersTable.firstName,
        stringLiteral(" "),
        UsersTable.lastName
    )
