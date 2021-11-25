package com.example.routes

import com.example.API_VERSION
import com.example.repo.StudentRepo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val CREATE_STUDENT = "$API_VERSION/student"

@OptIn(KtorExperimentalLocationsAPI::class)
@Location(CREATE_STUDENT)
class CreateStudent

@OptIn(KtorExperimentalLocationsAPI::class)
fun Route.student(
    db: StudentRepo
){
    post<CreateStudent> {
        val parameter = call.receive<Parameters>()

        val name = parameter["name"] ?: return@post call.respondText(
            "MISSING FIELD",
            status = HttpStatusCode.Unauthorized
        )
        val age = parameter["age"] ?: return@post call.respondText(
            "MISSING FIELD",
            status = HttpStatusCode.Unauthorized
        )
        try {
            val student = db.insert(name,age.toInt())

            student?.userId?.let {
                call.respond(status = HttpStatusCode.OK,student)
            }

        }catch (e: Throwable){
            call.respondText("${e.message}")
        }
    }

    get<CreateStudent> {
        try {
            val studentList = db.getAllStudents()
            call.respond(status = HttpStatusCode.OK,studentList)

        }catch (e: Throwable){
            call.respondText("${e.message}")
        }
    }

    delete("$API_VERSION/student/{id}"){
        val id = call.parameters["id"] ?: return@delete call.respondText(
            "no id",
            status = HttpStatusCode.Unauthorized
        )

        val result = db.deleteById(id.toInt())

       try {
           if (result == 1){
               call.respondText("$id deleted successfully..")
           }else{
               call.respondText("id not found")
           }
       }catch (e: Throwable){
           call.respondText("${e.message}")
       }
    }

    put("$API_VERSION/student/{id}") {

        val updateInfo = call.receive<Parameters>()

        val id = call.parameters["id"] ?: return@put call.respondText(
            "no id",
            status = HttpStatusCode.Unauthorized
        )

        val name = updateInfo["name"] ?: return@put call.respondText(
            "no name",
            status = HttpStatusCode.Unauthorized
        )
        val age = updateInfo["age"] ?: return@put call.respondText(
            "no id",
            status = HttpStatusCode.Unauthorized
        )

       try {
           val result = id.toInt().let {
               db.update(it,name,age.toInt())
           }
           if (result == 1){
               call.respondText("updated successfully....")
           }else{
               call.respondText("updated successfully....")
           }
       } catch (e: Throwable){
           application.log.error("Failed to register user", e)
           call.respond(HttpStatusCode.BadRequest, "Problems creating User")
       }
    }




}