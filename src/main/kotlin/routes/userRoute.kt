package routes

import Cache.CacheReposImp
import Client
import Interface.UserReposImp
import db.*
import connection.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*




fun Route.UserRoutes(userObject:UserReposImp , cacheObject : CacheReposImp){

    route("/user") {

        post("/create") {

            try {
                val userInput = call.receive<User>()

                if (userInput.email == null || userInput.name == null || userInput.password == null) {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorResponse("Fail to create","Please Provide all information"))
                    return@post
                }

                if(!userObject.isValidGmail(userInput.email)){
                    call.respond(HttpStatusCode.BadRequest, JsonErrorResponse("Fail to create","Email format is invalid"))
                    return@post
                }

                if (cacheObject.hexists("allEmail", userInput.email)) {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorResponse("Fail", "${userInput.email} already present"))
                    return@post
                }
                cacheObject.hset("allEmail", userInput.email, userInput.password)


                userObject.createUser(User(userInput.email, userInput.name, userInput.password))
                call.respond(HttpStatusCode.Created, JsonResponse("success", User(userInput.email, userInput.name, userInput.password)))
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.InternalServerError, JsonErrorResponse("Fail", "${ex.message}"))
            }
        }

        post("/delete") {
            try {
                val userInput = call.receive<Credential>()
                val email = userInput.email
                val password = userInput.password


                if (!(cacheObject.hexists("allEmail", email))) {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorResponse("Fail", "$email not exist"))
                    return@post
                }

                cacheObject.hdel("allEmail", email)
                val valuesToDelete = cacheObject.smembers("email:title:${userInput.email}")
                valuesToDelete.forEach { value ->
                    Client.jedis.del("title:$value")
                }
                cacheObject.del("email:title:${userInput.email}")
                cacheObject.del("AllTodo:${userInput.email}")


                val b = userObject.deleteUser(email, password)

                if (b) {
                    call.respond(HttpStatusCode.OK, JsonSuccess("successfully deleted"))
                } else call.respond(
                    HttpStatusCode.BadRequest,
                    JsonErrorResponse("Error in deletion", "$email don't exist or password is incorrect")
                )
            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.InternalServerError, response)
            }
        }

        put("/update") {
            try {
                val input = call.receive<UserUpdate>()

                val name = call.request.queryParameters["name"]
                val password = call.request.queryParameters["password"]

                userObject.Check(input.email, input.password)
                var flag = false
                if (name != null) {
                    flag = true
                    userObject.updateName(input.email, name)
                }
                if (password != null) {
                    flag = true
                    cacheObject.hset("allEmail", input.email, password)
                    userObject.updatePassword(input.email, password)

                }

                if (flag) call.respond(HttpStatusCode.OK, JsonSuccess("successfully updated"))
                else call.respond(HttpStatusCode.Forbidden, JsonSuccess("Nothing Updated"))


            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.InternalServerError, response)
            }
        }
    }


}