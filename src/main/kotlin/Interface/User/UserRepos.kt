package Interface.User

import DB.DataClasses.User

interface UserRepos{

    suspend fun createUser(user : User)
    suspend fun deleteUser(email: String , password : String): Boolean
    suspend fun updateName(email: String , name: String)
    suspend fun updatePassword(email: String , password: String)
    suspend fun Check(email: String , password: String)
    suspend fun update(email: String , name : String , password: String):Boolean
}