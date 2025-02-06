package db

// TODO : Package naming should be better
import kotlinx.serialization.Serializable

// FIXME: Redundant POJO Classes
@Serializable
data class ToDo(
    val email: String,
    val title : String,
    val description: String,
)

@Serializable
data class JsonTodo(
    val title: String,
    val description: String,
    val completed :Boolean,
    val date: String
)

@Serializable
data class JsonTodoDay(
    val title: String,
    val description: String,
    val completed :Boolean,
    val date: String
)

@Serializable
data class JsonResponseTodo(
    val status : String,
    val todo : JsonTodoDay
)

@Serializable
data class JsonInputDelete(
    val title : String,
    val email : String,
    val password: String
)

@Serializable
data class JsonSuccess(
    val status: String
)

@Serializable
data class JsonEmail(
    val email: String
)

@Serializable
data class JsonTodoList(
    val status: String,
    val todo : List<JsonTodo>
)

@Serializable
data class JsonTodoList2(
    val status: String,
    val todo : JsonTodo
)
@Serializable
data class JsonTodoDate(
    val email: String,
    val title: String,
    val password: String,
    val date: Long
)

@Serializable
data class JsonTodoDes(
    val email: String,
    val title: String,
    val password: String,
    val description: String
)
@Serializable
data class JsonTodoComp(
    val email: String,
    val title: String,
    val password: String,
)

@Serializable
data class JsonTitle(
    val title: String,
    val email: String,
    val password: String
)

@Serializable
data class JsonUpdate(
    val email: String,
    val title: String,
    val password: String
)

@Serializable
data class TodoAll(
    val email: String,
    val title: String,
    val description: String,
    val days:  Long,
    val password: String
)