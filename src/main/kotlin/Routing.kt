package com.example

import DI.Components.AppComponent
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.*

fun Application.configureRouting(appComponent: AppComponent) {
    routing {
        val todoRepos = appComponent.getTodoRepos()
        val userRepos = appComponent.getUserRepos()

        UserRoutes(userRepos)
        TodoRoutes(todoRepos , userRepos )



        get("/") {
            call.respondText("Hello World!")
        }

    }
}