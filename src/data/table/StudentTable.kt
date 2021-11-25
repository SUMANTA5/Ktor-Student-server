package com.example.data.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object StudentTable: Table() {

    val userId = integer("userId").autoIncrement()
    val name = varchar("name",512)
    val age = integer("age")

    override val primaryKey: PrimaryKey = PrimaryKey(userId)

}