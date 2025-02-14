package DB.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class JsonResponseTodo(
    val status : String,
    val todo : RequestJsonTodo
)

@Serializable
data class ResponseJsonTodoList(
    val status: String,
    val todo : List<RequestJsonTodo>
)

@Serializable
data class  JsonResponseUser(
    val status : String,
    val user : User
)