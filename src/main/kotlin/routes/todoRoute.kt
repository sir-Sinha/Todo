package routes

import db.*
import connection.dbOperation
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

fun addDaysToDate(timestamp: Long, daysToAdd: Long): Long {
    val instant = Instant.ofEpochMilli(timestamp)
    val updatedInstant = instant.plus(daysToAdd, ChronoUnit.DAYS)
    return updatedInstant.toEpochMilli()
}

fun formatDate(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return localDateTime.format(formatter)
}


fun Route.TodoRoutes(dbObject:dbOperation){

    post("/createTodo"){

        try{
            val todoInput = call.receive<TodoAll>()
            dbObject.addToDo(todoInput)
            val date = formatDate(addDaysToDate(Instant.now().toEpochMilli(), todoInput.days))
            if(todoInput.days < 0){
                throw IllegalArgumentException("Invalid date")
            }
            val response = JsonResponseTodo("success" , JsonTodoDay(
                todoInput.title ,
                todoInput.description ,
                false  ,
                date))
            call.respond(HttpStatusCode.Created , response)
        }catch (e:Exception){
            val response = JsonErrorResponse("Fail" , "${e.message}")
            call.respond(HttpStatusCode.BadRequest, response)
        }


    }
//
    get("/getTodo") {

        try{
            val inputEmail = call.receive<JsonEmail>()
            val email = inputEmail.email

            val todoList = dbObject.getAllTodo(email)
            if(todoList.size != 0){
                val response = JsonTodoList("success" , todoList)
                call.respond(HttpStatusCode.Found , response)
            }else {
                call.respond(HttpStatusCode.NotFound,JsonErrorResponse("success" ,"No Task to do!"))
            }
        }catch (e:Exception) {
            val response = JsonErrorResponse("Fail" , "${e.message}")
            call.respond(HttpStatusCode.BadRequest, response)
        }
    }

    get("/getTodoByTitle") {
        val inputTitle = call.receive<JsonTitle>()

        try{
            val todo = dbObject.getTodoByTitle(inputTitle)
            if(todo.size != 0){
                val response = JsonTodoList("success" , todo)
                call.respond(HttpStatusCode.Found , response)
            }else {
                call.respond(HttpStatusCode.NotFound,JsonErrorResponse("success" ,"No such title found to do!"))
            }
        }catch (e:Exception) {
            val response = JsonErrorResponse("Fail" , "${e.message}")
            call.respond(HttpStatusCode.BadRequest, response)
        }
    }


    post("/deleteTodo"){
        try{
             val todoDeleteInput = call.receive<JsonInputDelete>()
             val title = todoDeleteInput.title
             val email = todoDeleteInput.email
             val password = todoDeleteInput.password

            dbObject.Check(email , password)
            dbObject.deleteTodo(title, email)
            val response = JsonSuccess("success")
            call.respond(HttpStatusCode.OK ,response)
        }catch (e:Exception){
            val response = JsonErrorResponse("Fail" , "${e.message}")
            call.respond(HttpStatusCode.BadRequest, response)
        }
    }

    put("/updateTodo"){

        try{
             val input = call.receive<JsonUpdate>()

             val description = call.request.queryParameters["description"]
             val days = call.request.queryParameters["days"]
             val completed = call.request.queryParameters["completed"]
             var flag = false

            dbObject.Check(input.email ,input.password)

            if(days != null){
                var date = addDaysToDate(Instant.now().toEpochMilli(), days.toLong())
                if(date > Instant.now().toEpochMilli()) {
                    flag = true
                    dbObject.updateDate(JsonTodoDate(input.email, input.title, input.password, date.toLong()))
                }else{
                    throw IllegalArgumentException("Invalid Date!")
                }
            }

            if(description != null) {
                flag = true
                dbObject.updateDes(JsonTodoDes(input.email ,input.title, input.password ,description))

            }

            if(completed != null && completed == "true"){
                flag = true
                dbObject.updateCompletion(JsonTodoComp(input.email,input.title , input.password))
            }

            if(flag) call.respond(HttpStatusCode.OK  , JsonSuccess("success"))
            else call.respond(HttpStatusCode.Forbidden , JsonSuccess("Nothing Updated"))

        }catch (e:Exception){
            val response = JsonErrorResponse("Fail" , "${e.message}")
            call.respond(HttpStatusCode.BadRequest, response)
        }
    }

}