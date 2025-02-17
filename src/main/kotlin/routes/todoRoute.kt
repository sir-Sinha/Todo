package routes

import DB.DataClasses.*
import Interface.Todo.TodoRepos
import Interface.User.UserRepos
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.TodoRoutes(todoObject: TodoRepos, userObject : UserRepos){
    route("/todo") {
        post("/create") {

            try {
                val todoInput = call.receive<TodoAll>()

                val response = JsonResponseTodo(
                    "success", RequestJsonTodo(
                        todoInput.title,
                        todoInput.description,
                        false,
                        todoInput.days.toString()    /// Need to change
                    )
                )

                todoObject.addToDo(todoInput)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail to Fetch", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }


        }
//
        get("/get") {

            try {
                val inputEmail = call.receive<JsonEmailRequest>()
                val email = inputEmail.email
                val todoList = todoObject.getAllTodo(email)

//               cacheObject.set("AllTodo:$email", todoObject.serializeTodoList(todoList))

                if (todoList.size != 0)
                    call.respond(HttpStatusCode.Found, ResponseJsonTodoList("success", todoList))
                else
                    call.respond(HttpStatusCode.NotFound, JsonErrorResponse("success", "There is no task corresponding to $email!"))

            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail to Fetch", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }
        }

        get("/getByTitle") {
            val inputTitle = call.receive<JsonTitle>()

            try {
                val todo = todoObject.getTodoByTitle(inputTitle)
                if (todo.size != 0) {
                    call.respond(HttpStatusCode.Found, ResponseJsonTodoList("success", todo))
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
                val input = call.receive<JsonTitle>()

                val description = call.request.queryParameters["description"]?:""
                val days = call.request.queryParameters["days"]?:""
                val completed = call.request.queryParameters["completed"]?:""

                userObject.Check(input.email, input.password)
                val result = todoObject.update(input , description , days , completed)


                if (result) {
                    call.respond(HttpStatusCode.OK, JsonSuccess("success"))
                } else call.respond(HttpStatusCode.Forbidden, JsonSuccess("You have updated nothing"))


            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.BadRequest, response)
            }
        }
    }

}