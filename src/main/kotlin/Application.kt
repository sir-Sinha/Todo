
package com.example



import io.ktor.server.application.*
import io.ktor.server.engine.*
import connection.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.server.netty.*



fun main(args: Array<String>) {

    EngineMain.main(args)


}

fun Application.module() {

    install(ContentNegotiation) {
        json(Json {
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    DatabaseFactory.init()
    Client.jedis

    configureSerialization()
    configureSecurity()
    configureRouting()
}
