package db

import db.userTable.nullable
import org.jetbrains.exposed.sql.Table

object userTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).nullable()
    val email = varchar("email" , 255).uniqueIndex()
    var password = varchar("password" , 255)

    override val primaryKey = PrimaryKey(email)
}