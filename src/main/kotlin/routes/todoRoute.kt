package routes

import Cache.CacheReposImp
import Client
import Interface.TodoRepos
import Interface.TodoReposImp
import Interface.UserReposImp
import db.*
import connection.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


fun Route.TodoRoutes(todoObject:TodoReposImp , userObject : UserReposImp , cacheObject : CacheReposImp){
    route("/todo") {
        post("/create") {

            try {
                val todoInput = call.receive<TodoAll>()
                val date = todoObject.formatDate(todoObject.addDaysToDate(Instant.now().toEpochMilli(), todoInput.days))

                if (todoInput.days < 0) {
                    throw IllegalArgumentException("Invalid date")
                }

                val response = JsonResponseTodo(
                    "success", JsonTodoDay(
                        todoInput.title,
                        todoInput.description,
                        false,
                        date
                    )
                )

                cacheObject.set(
                    "title:${todoInput.title}", kotlinx.serialization.json.Json.encodeToString(
                        JsonTodoDay(
                            todoInput.title,
                            todoInput.description,
                            false,
                            date
                        )
                    )
                )
                Client.jedis.sadd("email:title:${todoInput.email}", todoInput.title)

                todoObject.addToDo(todoInput)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }


        }
//
        get("/get") {

            try {
                val inputEmail = call.receive<JsonEmail>()
                val email = inputEmail.email
                val cachedTodoList = Client.jedis.get("AllTodo:$email")

                if (cachedTodoList != null) {
                    val todoList = todoObject.parseTodoList(cachedTodoList) // You need to deserialize the cached data
                    call.respond(HttpStatusCode.Found, JsonTodoList("success", todoList))
                    return@get
                }

                val todoList = todoObject.getAllTodo(email)
                cacheObject.set("AllTodo:$email", todoObject.serializeTodoList(todoList))

                if (todoList.size != 0)
                    call.respond(HttpStatusCode.Found, JsonTodoList("success", todoList))
                else
                    call.respond(HttpStatusCode.NotFound, JsonErrorResponse("success", "No Task to do!"))

            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }
        }

        get("/getByTitle") {
            val inputTitle = call.receive<JsonTitle>()

            try {
                if (!cacheObject.hexists("allEmail", inputTitle.email) ||
                    Client.jedis.hget("allEmail", inputTitle.email) != inputTitle.password
                ) {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorResponse("Error", "Invalid email or password!"))
                }
                val cachedTodo = Client.jedis.get("title:${inputTitle.title}")
                if (cachedTodo != null) {
                    call.respond(HttpStatusCode.OK, JsonTodoList2("success", todoObject.parseTodo(cachedTodo)))
                    return@get
                }
                val todo = todoObject.getTodoByTitle(inputTitle)
                if (todo.size != 0) {
                    val response = JsonTodoList("success", todo)
                    cacheObject.set("title:${inputTitle.title}", kotlinx.serialization.json.Json.encodeToString(todo))
                    call.respond(HttpStatusCode.Found, response)
                } else {
                    call.respond(HttpStatusCode.NotFound, JsonErrorResponse("success", "No such title found to do!"))
                }
            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }
        }


        post("/delete") {
            try {
                val todoDeleteInput = call.receive<JsonInputDelete>()


                cacheObject.del("title:${todoDeleteInput.title}")
                cacheObject.del("AllTodo:${todoDeleteInput.email}")
                cacheObject.srem("email:title:${todoDeleteInput.email}", todoDeleteInput.title)

                userObject.Check(todoDeleteInput.email, todoDeleteInput.password)
                todoObject.deleteTodo(todoDeleteInput.title, todoDeleteInput.email)

                call.respond(HttpStatusCode.OK, JsonSuccess("success"))
            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }
        }

        put("/update") {

            try {
                val input = call.receive<JsonUpdate>()

                val description = call.request.queryParameters["description"]
                val days = call.request.queryParameters["days"]
                val completed = call.request.queryParameters["completed"]
                var flag = false

                userObject.Check(input.email, input.password)

                if (days != null) {
                    var date = todoObject.addDaysToDate(Instant.now().toEpochMilli(), days.toLong())
                    if (date > Instant.now().toEpochMilli()) {
                        flag = true
                        todoObject.updateDate(JsonTodoDate(input.email, input.title, input.password, date.toLong()))
                    } else {
                        throw IllegalArgumentException("Invalid Date!")
                    }
                }

                if (description != null) {
                    flag = true
                    todoObject.updateDes(JsonTodoDes(input.email, input.title, input.password, description))

                }

                if (completed != null && completed == "true") {
                    flag = true
                    todoObject.updateCompletion(JsonTodoComp(input.email, input.title, input.password))
                }

                if (flag) {

                    val resp = todoObject.getTodoByTitle(
                        JsonTitle(
                            input.title,
                            input.email,
                            input.password
                        )
                    )
                    if (resp != null)
                        cacheObject.set("title:${input.title}", kotlinx.serialization.json.Json.encodeToString(resp))
                    cacheObject.del("AllTodo:${input.email}")
                    call.respond(HttpStatusCode.OK, JsonSuccess("success"))
                } else call.respond(HttpStatusCode.Forbidden, JsonSuccess("Nothing Updated"))


            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }
        }
    }

}