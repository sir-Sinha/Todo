package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection
import java.sql.DriverManager
import connection.dbOperation
import routes.*
import db.*

fun Application.configureRouting() {
    routing {
        val dbObject = dbOperation()

        UserRoutes(dbObject)
        TodoRoutes(dbObject)



        get("/") {
            call.respondText("Hello World!")
        }


    }
}
