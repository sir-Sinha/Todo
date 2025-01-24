package connection

import db.*
import connection.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

fun addDaysToDate(timestamp: Long, daysToAdd: Long): Long {
    val instant = Instant.ofEpochMilli(timestamp)  // Convert the timestamp (Long) to Instant
    val updatedInstant = instant.plus(daysToAdd, ChronoUnit.DAYS)  // Add days to the instant
    return updatedInstant.toEpochMilli()  // Convert back to milliseconds and return
}

fun formatDate(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return localDateTime.format(formatter)
}

class dbOperation {

    suspend fun insert(user:User) {
        dbQuery {
            userTable.insert {
                it[email] = user.email
                it[name] = user.name
                it[password] = user.password
            }
        }
    }

//    private  fun deleteTodoByUserId( : Int)
//    {
//        todoTable.deleteWhere {
//            todoTable.userId.eq(userId)
//        }
//
//    }



    suspend fun deleteUser(email:String,password:String) =
        dbQuery {
//            deleteTodoByUserId(FindUser(email))
            userTable.deleteWhere {
                userTable.password.eq(password) and userTable.email.eq(email)
            } > 0
//            val userId = userTable
//                .slice(userTable.id)
//                .select {
//                    userTable.email.eq(email) and userTable.password.eq(password)
//                }
//                .singleOrNull()
//                ?.get(userTable.id)
//
//
//            if (userId != null) {
//
//                deleteTodoByUserId(userId)
//                userTable.deleteWhere {
//                    userTable.id.eq(userId)
//                }
//                true
//            } else {
//                false
//            }
        }

    suspend fun updateName(email: String , Name: String){
        dbQuery {

            userTable.update(
                where = {
                    userTable.email.eq(email)
                }
            ){
                it[name] = Name
            }
        }
    }
    suspend fun updatePassword(email: String , Password: String){
        dbQuery {

            userTable.update(
                where = {
                    userTable.email.eq(email)
                }
            ){
                it[password] = Password
            }
        }
    }







    private fun rowToUser(row: ResultRow?):User?
    {
        if(row==null)
            return null
        return User(
            email = row[userTable.email],
            name = row[userTable.name].toString(),
            password = row[userTable.password]
        )
    }

    suspend fun findUserByEmail(email:String)
            =
        dbQuery {
            userTable.select{
                userTable.email.eq(email)
            }.map { rowToUser(it) }
                .singleOrNull()

        }




    suspend fun updateDate(todo: JsonTodoDate){
        dbQuery {
            todoTable.update(
                where = {
                    todoTable.email.eq(todo.email) and todoTable.title.eq(todo.title)
                }
            ){
                it[date] = todo.date
            }
        }
    }
    suspend fun Check(email: String , password: String)
        =
        dbQuery {
            val userExists = userTable
                .slice(userTable.email)
                .select { userTable.email.eq(email) and userTable.password.eq(password) }
                .singleOrNull() != null

            if (!userExists) {
                throw IllegalArgumentException("User with email ${email} does not exist or password is incorrect.")
            }
        }
    suspend fun updateDes(todo :JsonTodoDes){
        dbQuery {

            todoTable.update(
                where = {
                    todoTable.email.eq(todo.email) and todoTable.title.eq(todo.title)
                }
            ){
                it[description] = todo.description
            }
        }
    }
    suspend fun updateCompletion(todo : JsonTodoComp){
        dbQuery {

            todoTable.update(
                where = {
                    todoTable.email.eq(todo.email) and todoTable.title.eq(todo.title)
                }
            ){
                it[completed] = true
            }
        }
    }

    suspend fun deleteTodo(title : String,email: String) =
        dbQuery {
            todoTable.deleteWhere {
                todoTable.title.eq(title) and todoTable.email.eq(email)
            }>0

        }

    suspend fun addToDo(todo:TodoAll) {
        dbQuery {
            val userExists = userTable
                .slice(userTable.email)
                .select { userTable.email.eq(todo.email) and userTable.password.eq(todo.password) }
                .singleOrNull() != null

            if (!userExists) {
                throw IllegalArgumentException("User with email ${todo.email} does not exist or password is incorrect.")
            }

            val dateInMillis = addDaysToDate(Instant.now().toEpochMilli(), todo.days)

                todoTable.insert {
                    it[email] = todo.email
                    it[title] = todo.title
                    it[description] = todo.description
                    it[date] = dateInMillis
                }
        }
    }

    private fun rowToDos(row: ResultRow?):JsonTodo?
    {
        if(row==null)
            return null
        return JsonTodo(
            date = formatDate(row[todoTable.date]),
            title = row[todoTable.title],
            completed = row[todoTable.completed],
            description = row[todoTable.description].toString()
        )
    }

    suspend fun getAllTodo(email: String): List<JsonTodo>
            =
        dbQuery {
            todoTable.select{
                todoTable.email.eq(email)
            }.mapNotNull { rowToDos(it) }


        }

    suspend fun getTodoByTitle(todo : JsonTitle)
        =
        dbQuery {
            val userExists = userTable
                .slice(userTable.email)
                .select { userTable.email eq todo.email }
                .singleOrNull() != null

            if (!userExists) {
                throw IllegalArgumentException("User with email ${todo.email} does not exist or password is incorrect.")
            }

            todoTable.select{
                todoTable.email.eq(todo.email) and todoTable.title.eq(todo.title)
            }.mapNotNull { rowToDos(it) }

        }





}