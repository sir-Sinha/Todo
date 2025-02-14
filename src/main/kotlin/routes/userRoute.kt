package routes

import DB.DataClasses.*
import Interface.User.UserRepos
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*




fun Route.UserRoutes(userObject: UserRepos){

    route("/user") {

        post("/create") {

            try {
                val userInput = call.receive<User>()

                userObject.createUser(User(userInput.email, userInput.name, userInput.password))
                call.respond(HttpStatusCode.Created, JsonResponseUser("success", User(userInput.email, userInput.name, userInput.password)))
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.InternalServerError, JsonErrorResponse("Fail", "${ex.message}"))
            }
        }

        post("/delete") {
            try {
                val userInput = call.receive<Credential>()
                val email = userInput.email
                val password = userInput.password


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
                val input = call.receive<Credential>()

                val name = call.request.queryParameters["name"]?:""
                val password = call.request.queryParameters["password"]?:""

                userObject.Check(input.email, input.password)
                val res = userObject.update(input.email, name , password)

                if (res) call.respond(HttpStatusCode.OK, JsonSuccess("Successfully Updated!"))
                else call.respond(HttpStatusCode.Forbidden, JsonSuccess("You have updated nothing."))


            } catch (e: Exception) {
                val response = JsonErrorResponse("Fail", "${e.message}")
                call.respond(HttpStatusCode.InternalServerError, response)
            }
        }
    }


}