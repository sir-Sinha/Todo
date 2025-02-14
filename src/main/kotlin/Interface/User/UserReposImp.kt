package Interface.User

import Cache.CacheRepos
import connection.*
import DB.DataClasses.User
import DB.Tables.UserTable
import Interface.TodoUtils.TodoUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import javax.inject.Inject

class UserReposImp  @Inject constructor(private val todoUtils: TodoUtils, private val cacheRepos: CacheRepos) :
    UserRepos {

    override suspend fun createUser(user: User){
        if (user.email == "" || user.name == "" || user.password == "") {
            throw IllegalArgumentException("Detail is insufficient in order to create a you profile")
        }

        if(!todoUtils.isValidGmail(user.email)){
            throw IllegalArgumentException("Email format is invalid.Please try again with valid email!")
        }

        if (cacheRepos.hexists("allEmail", user.email)) {
            throw IllegalArgumentException("${user.email} already present")
        }
        cacheRepos.hset("allEmail", user.email, user.password)
        dbTransaction {

                UserTable.insert {
                    it[email] = user.email
                    it[name] = user.name
                    it[password] = user.password
                }

        }
    }

    override suspend fun deleteUser(email:String,password:String) =
        dbTransaction {
            Check(email , password)

            cacheRepos.hdel("allEmail", email)
            val valuesToDelete = cacheRepos.smembers("email:title:${email}")
            valuesToDelete.forEach { value ->
                cacheRepos.del("title:$value")
            }
            cacheRepos.del("email:title:${email}")
            cacheRepos.del("AllTodo:${email}")

            UserTable.deleteWhere {
                UserTable.password.eq(password) and UserTable.email.eq(email)
            } > 0
        }
    override suspend fun updateName(email: String , Name: String){
        dbTransaction {

            UserTable.update(
                where = {
                    UserTable.email.eq(email)
                }
            ){
                it[name] = Name
            }
        }
    }

    override suspend fun updatePassword(email: String , Password: String) {
        dbTransaction {

            UserTable.update(
                where = {
                    UserTable.email.eq(email)
                }
            ) {
                it[password] = Password
            }
            cacheRepos.hset("allEmail", email, Password)
        }
    }

    override suspend fun Check(email: String , password: String)
            =
        dbTransaction {
            val userExists = UserTable
                .slice(UserTable.email)
                .select { UserTable.email.eq(email) and UserTable.password.eq(password) }
                .singleOrNull() != null

            if (!userExists) {
                throw IllegalArgumentException("User with email ${email} does not exist or password is incorrect.")
            }
        }

    override suspend fun update(email: String , name : String , password: String):Boolean {

            var flag = false
            if (name != "") {
                flag = true
                updateName(email, name)
            }
            if (password != "") {
                flag = true
                updatePassword(email, password)

            }
            return flag

    }
}