package com.lingou

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.time.Duration

const val REST_ENDPOINT = "/lingous"

fun Application.main() {
    install(DefaultHeaders)
    install(CORS) {
        maxAgeInSeconds = Duration.ofDays(1).seconds
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }

        routing {
            get("$REST_ENDPOINT/{idIngou}") {
                errorAware {
                    val id = call.parameters["idIngou"] ?: throw IllegalArgumentException("Parameter id not found")
                    call.respond(LingouRepo.get(id))
                }
            }

            get(REST_ENDPOINT) {
                errorAware {
                    println("Received Get All Request")
                    call.respond(LingouRepo.getAll())
                }
            }

            delete("$REST_ENDPOINT/{idIngou}") {
                errorAware {
                    val id = call.parameters["idIngou"] ?: throw IllegalArgumentException("Parameter id not found")
                    call.respond(LingouRepo.remove(id))
                }
            }

            delete(REST_ENDPOINT) {
                errorAware {
                    LingouRepo.clear()
                    call.respondSuccessJson()
                }
            }

            post(REST_ENDPOINT) {
                errorAware {
                    val receive = call.receive<Ingou>()
                    println("Received Post Request: $receive")
                    call.respond(LingouRepo.add(receive))
                }
            }

            get("/") {
                errorAware {
                    call.respondText{
                       "Api working!"
                    }
                }
            }

        }
    }
}

private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText(
            """{"error": "$e"}""",
            ContentType.parse("application/json"),
            HttpStatusCode.InternalServerError
        )
        null
    }
}

private suspend fun ApplicationCall.respondSuccessJson(value: Boolean = true) = respond("""{"success": "$value"}""")