package com.example

import Cache.CacheReposImp
import Interface.TodoReposImp
import Interface.UserReposImp
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
import connection.*
import routes.*
import db.*

fun Application.configureRouting() {
    routing {
        // FIXME: Instead of using of concrete class inject interfaces of service classes
        // FIXME: Dependency Injection is not done properly
        val todoObject = TodoReposImp()
        val userObject = UserReposImp()
        val cacheObject = CacheReposImp()
        UserRoutes(userObject , cacheObject)
        TodoRoutes(todoObject , userObject , cacheObject)



        get("/") {
            call.respondText("Hello World!")
        }


    }
}