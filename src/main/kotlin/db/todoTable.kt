package db

import jdk.jfr.internal.event.EventConfiguration.timestamp
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object todoTable: Table() {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).references(userTable.email , onDelete = ReferenceOption.CASCADE)
    val title = varchar("title" , 255)
    val description = text("description").nullable()
    var completed = bool("completed").default(false)
    val date = long("date")

    override val primaryKey = PrimaryKey(email , title)





}