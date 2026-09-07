package de.malteans.leisureactivities.db.ext

import de.malteans.leisureactivities.db.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.concat
import org.jetbrains.exposed.sql.stringLiteral

val UsersTable.displayName
    get() = concat(
        UsersTable.firstName,
        stringLiteral(" "),
        UsersTable.lastName
    )
