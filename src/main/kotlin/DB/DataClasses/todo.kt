package DB.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class TodoAll(
    val email: String,
    val title: String,
    val description: String,
    val days:  Long,
    val password: String
)



@Serializable
data class JsonTitle(
    val title: String,
    val email: String,
    val password: String
)


