package DB.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class JsonSuccess(
    val status: String
)

@Serializable
data class JsonErrorResponse(
    val status : String,
    val message : String
)