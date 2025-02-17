package DB.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email:String,
    val name:String,
    val password:String,
)

@Serializable
data class Credential(
    val email: String,
    val password: String
)