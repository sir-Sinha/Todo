package DB.Tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object TodoTable: Table() {
    val id = integer("id").autoIncrement()
    val email = varchar("email" , 255).references(UserTable.email, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title" , 255).uniqueIndex()
    val description = text("description").nullable()
    var completed = bool("completed").default(false)
    val date = long("date")

    override val primaryKey = PrimaryKey(id)





}