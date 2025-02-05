package Interface

import db.*

interface TodoRepos{
    fun addDaysToDate(timestamp: Long, daysToAdd: Long): Long
    fun formatDate(timestamp: Long): String
    suspend fun addToDo(todo : TodoAll)
    suspend fun getAllTodo(email: String): List<JsonTodo>
    suspend fun getTodoByTitle(input: JsonTitle): List<JsonTodo>
    suspend fun deleteTodo(title : String , email: String): Boolean
    fun serializeTodoList(todoList: List<JsonTodo>): String
    fun parseTodoList(cachedTodoList: String): List<JsonTodo>
    fun parseTodo(cachedTodoList: String): JsonTodo
    suspend fun updateDate(todo: JsonTodoDate)
    suspend fun updateDes(todo :JsonTodoDes)
    suspend fun updateCompletion(todo : JsonTodoComp)

}