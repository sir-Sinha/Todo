package DB.Tables


import org.jetbrains.exposed.sql.Table

object UserTable : Table() {
    val userid = integer("userid").autoIncrement()
    val name = varchar("name", 255).nullable()
    val email = varchar("email" , 255).uniqueIndex()
    var password = varchar("password" , 255)

    override val primaryKey = PrimaryKey(userid)
}