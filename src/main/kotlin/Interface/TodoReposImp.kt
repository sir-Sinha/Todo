package Interface

import connection.*
import db.*
import kotlinx.serialization.encodeToString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class TodoReposImp : TodoRepos{
    override fun addDaysToDate(timestamp: Long, daysToAdd: Long): Long {
        val instant = Instant.ofEpochMilli(timestamp)
        val updatedInstant = instant.plus(daysToAdd, ChronoUnit.DAYS)
        return updatedInstant.toEpochMilli()
    }

    override fun formatDate(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDateTime.format(formatter)
    }

    override suspend fun addToDo(todo:TodoAll) {
        dbQuery {
            val userExists = userTable
                .slice(userTable.email)
                .select { userTable.email.eq(todo.email) and userTable.password.eq(todo.password) }
                .singleOrNull() != null

            if (!userExists) {
                throw IllegalArgumentException("User with email ${todo.email} does not exist or password is incorrect.")
            }

            val dateInMillis = connection.addDaysToDate(Instant.now().toEpochMilli(), todo.days)

            todoTable.insert {
                it[email] = todo.email
                it[title] = todo.title
                it[description] = todo.description
                it[date] = dateInMillis
            }
        }
    }

    override suspend fun deleteTodo(title : String,email: String) =
        dbQuery {
            todoTable.deleteWhere {
                todoTable.title.eq(title) and todoTable.email.eq(email)
            }>0

        }
    override suspend fun getAllTodo(email: String): List<JsonTodo>
            =
        dbQuery {
            todoTable.select{
                todoTable.email.eq(email)
            }.mapNotNull { row ->
                row?.let {
                    JsonTodo(
                        date = formatDate(it[todoTable.date]),
                        title = it[todoTable.title],
                        completed = it[todoTable.completed],
                        description = it[todoTable.description].toString()
                    )
                }
            }


        }

    override suspend fun getTodoByTitle(todo: JsonTitle): List<JsonTodo> = dbQuery {
        val userExists = userTable
            .slice(userTable.email)
            .select { userTable.email eq todo.email }
            .singleOrNull() != null

        if (!userExists) {
            throw IllegalArgumentException("User with email ${todo.email} does not exist or password is incorrect.")
        }

        todoTable.select {
            (todoTable.email eq todo.email) and (todoTable.title eq todo.title)
        }.mapNotNull { row ->
            JsonTodo(
                date = formatDate(row[todoTable.date]),
                title = row[todoTable.title],
                completed = row[todoTable.completed],
                description = row[todoTable.description].toString()
            )
        }
    }

    override suspend fun updateDate(todo: JsonTodoDate){
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

    override suspend fun updateDes(todo :JsonTodoDes){
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

    override suspend fun updateCompletion(todo : JsonTodoComp){
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

    override fun serializeTodoList(todoList: List<JsonTodo>): String {
        return kotlinx.serialization.json.Json.encodeToString(todoList)
    }

    override fun parseTodoList(cachedTodoList: String): List<JsonTodo> {
        return kotlinx.serialization.json.Json.decodeFromString(cachedTodoList)
    }

    override fun parseTodo(cachedTodoList: String): JsonTodo {
        return kotlinx.serialization.json.Json.decodeFromString(cachedTodoList)
    }

}