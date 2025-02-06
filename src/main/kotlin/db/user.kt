package db

import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

// FIXME: Create separate POJO classes for each
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

@Serializable
data class UserDetails(
    val id : Int,
    val email:String,
    val name:String,
    val password:String,
)
// FIXME: POJO Class naming convention should be sensible
@Serializable
data class  JsonResponse(
    val status : String,
    val user : User
)

@Serializable
data class JsonErrorResponse(
    val status : String,
    val message : String
)

@Serializable
data class UserUpdate(
    val email : String,
    val password: String
)