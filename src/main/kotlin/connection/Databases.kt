package connection

import db.User
import db.userTable
import db.todoTable

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init()
    {
        // FIXME: Read Config Values from properties file
        Database.connect(
            "jdbc:postgresql://ep-purple-water-a5lenduw.us-east-2.aws.neon.tech/Test_DB",
            user = "Test_DB_owner",
            password = "npg_5xYpuAFzkN2E"
        )

        transaction{
            SchemaUtils.create(userTable)
            SchemaUtils.create(todoTable)
        }
    }





}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }