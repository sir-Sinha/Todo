package Interface

import connection.*
import db.User
import db.userTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

//FIXME: All DB operations should be performed transactional
class UserReposImp : UserRepos {

    override fun isValidGmail(email: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_.+-]+@gmail\\.com$")
        return regex.matches(email)
    }

    override suspend fun createUser(user: User){
        //FIXME: database actions should transactional
        dbQuery {
            userTable.insert {
                it[email] = user.email
                it[name] = user.name
                it[password] = user.password
            }
        }
    }

    // TODO: Search and Delete user by their Id not not after making Id as Primary Key
    override suspend fun deleteUser(email:String,password:String) =
        dbQuery {
//            deleteTodoByUserId(FindUser(email))
            userTable.deleteWhere {
                userTable.password.eq(password) and userTable.email.eq(email)
            } > 0
        }
    override suspend fun updateName(email: String , Name: String){
        dbQuery {

            userTable.update(
                where = {
                    userTable.email.eq(email)
                }
            ){
                it[name] = Name
            }
        }
    }

    override suspend fun updatePassword(email: String , Password: String) {
        dbQuery {

            userTable.update(
                where = {
                    userTable.email.eq(email)
                }
            ) {
                it[password] = Password
            }
        }
    }

    override suspend fun Check(email: String , password: String)
            =
        dbQuery {
            val userExists = userTable
                .slice(userTable.email)
                .select { userTable.email.eq(email) and userTable.password.eq(password) }
                .singleOrNull() != null

            if (!userExists) {
                throw IllegalArgumentException("User with email ${email} does not exist or password is incorrect.")
            }
        }
}