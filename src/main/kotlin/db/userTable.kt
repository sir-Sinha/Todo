package db

import db.userTable.nullable
import org.jetbrains.exposed.sql.Table

// TODO: Follow Class Naming Convention, Classes always Start with Capital letters only
object userTable : Table() {
    // FIXME: Why id column is created and not used?
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).nullable() // TODO: Make name Non Nullable or remove the column
    val email = varchar("email" , 255).uniqueIndex()
    var password = varchar("password" , 255)

    override val primaryKey = PrimaryKey(email) //FIXME: Use UUID for making primary key and give reference of this primary key to another tables for foreign key
}