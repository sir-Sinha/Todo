package Interface.TodoUtils

import DB.DataClasses.RequestJsonTodo
import kotlinx.serialization.encodeToString
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class TodoUtilsImp @Inject constructor(): TodoUtils {
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
    override fun isValidGmail(email: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_.+-]+@gmail\\.com$")
        return regex.matches(email)
    }

    override fun serializeTodoList(todoList: List<RequestJsonTodo>): String {
        return kotlinx.serialization.json.Json.encodeToString(todoList)
    }

    override fun parseTodoList(cachedTodoList: String): List<RequestJsonTodo> {
        return kotlinx.serialization.json.Json.decodeFromString(cachedTodoList)
    }

    override fun parseTodo(cachedTodoList: String): RequestJsonTodo {
        return kotlinx.serialization.json.Json.decodeFromString(cachedTodoList)
    }
}