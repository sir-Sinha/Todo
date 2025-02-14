package Interface.TodoUtils

import DB.DataClasses.RequestJsonTodo

interface TodoUtils {
    fun addDaysToDate(timestamp: Long, daysToAdd: Long): Long
    fun formatDate(timestamp: Long): String
    fun isValidGmail(email: String): Boolean
    fun serializeTodoList(todoList: List<RequestJsonTodo>): String
    fun parseTodoList(cachedTodoList: String): List<RequestJsonTodo>
    fun parseTodo(cachedTodoList: String): RequestJsonTodo
}