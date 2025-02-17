package Interface.Todo

import Cache.CacheRepos
import connection.*
import DB.DataClasses.JsonTitle
import DB.DataClasses.RequestJsonTodo
import DB.DataClasses.TodoAll
import DB.Tables.TodoTable
import DB.Tables.UserTable
import Interface.TodoUtils.TodoUtils
import kotlinx.serialization.encodeToString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import javax.inject.Inject


class TodoReposImp @Inject constructor(private val todoUtils: TodoUtils, private val cacheRepos: CacheRepos) :
    TodoRepos {


    override suspend fun addToDo(todo: TodoAll) {
        val fmtdate = todoUtils.formatDate(todoUtils.addDaysToDate(Instant.now().toEpochMilli(), todo.days))

        if (todo.days < 0) {
            throw IllegalArgumentException("Invalid date in input")
        }

        cacheRepos.set(
            "title:${todo.title}", kotlinx.serialization.json.Json.encodeToString(
                RequestJsonTodo(
                    todo.title,
                    todo.description,
                    false,
                    fmtdate
                )
            )
        )

        cacheRepos.sadd("email:title:${todo.email}", todo.title)

        val userExists = UserTable
            .slice(UserTable.email)
            .select { UserTable.email.eq(todo.email) and UserTable.password.eq(todo.password) }
            .singleOrNull() != null

        if (!userExists) {
            throw IllegalArgumentException("User with email ${todo.email} does not exist or password is incorrect.")
        }

        val dateInMillis = todoUtils.addDaysToDate(Instant.now().toEpochMilli(), todo.days)
        dbTransaction {

            TodoTable.insert {
                it[email] = todo.email
                it[title] = todo.title
                it[description] = todo.description
                it[date] = dateInMillis
            }

        }
    }

    override suspend fun deleteTodo(title : String,email: String) =
        dbTransaction {
            cacheRepos.del("title:${title}")
            cacheRepos.del("AllTodo:${email}")
            cacheRepos.srem("email:title:${email}", title)


            TodoTable.deleteWhere {
                TodoTable.title.eq(title) and TodoTable.email.eq(email)
            }>0

        }
    override suspend fun getAllTodo(email: String): List<RequestJsonTodo>
            =
        dbTransaction {
            val cachedTodoList = cacheRepos.get("AllTodo:$email")

            if (cachedTodoList != null) {
                val todoList = todoUtils.parseTodoList(cachedTodoList) // You need to deserialize the cached data
                return@dbTransaction todoList
            }

//            cacheRepos.set("AllTodo:$email", todoUtils.serializeTodoList(todoList))
            transaction {
                TodoTable.select {
                    TodoTable.email.eq(email)
                }.mapNotNull { row ->
                    row?.let {
                        RequestJsonTodo(
                            date = todoUtils.formatDate(it[TodoTable.date]),
                            title = it[TodoTable.title],
                            completed = it[TodoTable.completed],
                            description = it[TodoTable.description].toString()
                        )
                    }
                }
            }


        }

    override suspend fun getTodoByTitle(todo: JsonTitle): List<RequestJsonTodo>{
        val cachedTodo = Client.jedis.get("title:${todo.title}")
        var resp = listOf(todoUtils.parseTodo(cachedTodo))
        if (cachedTodo != null) {
            return resp
        }

        val userExists = UserTable
            .slice(UserTable.email)
            .select { UserTable.email eq todo.email }
            .singleOrNull() != null

        if (!userExists) {
            throw IllegalArgumentException("User with email ${todo.email} does not exist or password is incorrect.")
        }

        dbTransaction {

                resp = TodoTable.select {
                    (TodoTable.email eq todo.email) and (TodoTable.title eq todo.title)
                }.mapNotNull { row ->
                    RequestJsonTodo(
                        date = todoUtils.formatDate(row[TodoTable.date]),
                        title = row[TodoTable.title],
                        completed = row[TodoTable.completed],
                        description = row[TodoTable.description].toString()
                    )
                }
                cacheRepos.set("title:${todo.title}", kotlinx.serialization.json.Json.encodeToString(resp))


        }

        return resp
    }

    override suspend fun updateDate(email:String , title: String , password:String , days : Long){
        dbTransaction {
            var dates = todoUtils.addDaysToDate(Instant.now().toEpochMilli(), days.toLong())
            if (dates < Instant.now().toEpochMilli()) {
                throw IllegalArgumentException("Invalid Date!")
            }

            TodoTable.update(
                where = {
                    TodoTable.email.eq(email) and TodoTable.title.eq(title)
                }
            ){
                it[date] = dates
            }
        }
    }

    override suspend fun updateDes(email: String , title: String , password: String , desc: String){
        dbTransaction {

            TodoTable.update(
                where = {
                    TodoTable.email.eq(email) and TodoTable.title.eq(title)
                }
            ){
                it[description] = desc
            }
        }
    }

    override suspend fun updateCompletion(email: String , title:String){
        dbTransaction {

            TodoTable.update(
                where = {
                    TodoTable.email.eq(email) and TodoTable.title.eq(title)
                }
            ){
                it[completed] = true
            }
        }
    }

    override suspend fun update(todo : JsonTitle, description: String, days : String, completed: String):Boolean{

            var flag = false

            if (days != "") {
                flag = true
                updateDate(todo.email, todo.title, todo.password, days.toLong())
            }
            if (description != "") {
                flag = true
                updateDes(todo.email, todo.title, todo.password, description)
            }

            if (completed != "" && completed == "true") {
                flag = true
                updateCompletion(todo.email, todo.title)
            }

            cacheRepos.set("title:${todo.title}", kotlinx.serialization.json.Json.encodeToString(getTodoByTitle(
                    JsonTitle(
                        todo.title,
                        todo.email,
                        todo.password
                    )
                    )))
            cacheRepos.del("AllTodo:${todo.email}")

            return flag

    }


}