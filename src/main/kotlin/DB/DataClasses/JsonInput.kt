package DB.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class RequestJsonTodo(
    val title: String,
    val description: String,
    val completed :Boolean,
    val date: String
)

@Serializable
data class JsonInputDelete(
    val title : String,
    val email : String,
    val password: String
)

@Serializable
data class JsonEmailRequest(
    val email: String
)




