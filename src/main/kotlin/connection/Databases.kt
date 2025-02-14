package connection

import DB.Tables.UserTable
import DB.Tables.TodoTable

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object DatabaseFactory {
    fun init()
    {
        Database.connect(
            System.getenv("DB_URL"),
            user = System.getenv("DB_USER"),
            password = System.getenv("DB_PASSWORD")
        )

        transaction{
            SchemaUtils.create(UserTable)
            SchemaUtils.create(TodoTable)
        }
    }





}

suspend fun <T> dbTransaction(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }