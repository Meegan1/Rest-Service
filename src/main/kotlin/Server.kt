package me.meegan.rest

import com.beust.klaxon.Klaxon
import me.meegan.rest.utils.HTTPCommandUtil
import java.io.StringReader
import java.time.LocalDateTime
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


fun main() {

    val tasks: HashMap<String, String> = HashMap()
    val server = HTTPServerHandler()

    server.registerCommand("hi", object: ResourceCallback {
        override fun get(data: ContainerRequestContext): Response {
            return Response.ok("Hello World!").build()
        }

        override fun post(data: ContainerRequestContext): Response {
            return Response.ok().build()
        }
    })

    server.registerCommand("tasks", object : ResourceCallback {
        override fun post(data: ContainerRequestContext): Response {
            val taskName = HTTPCommandUtil().getQueryParams(data).getFirst("task-name")
            val taskDetails = HTTPCommandUtil().getQueryParams(data).getFirst("task-details")

            tasks.put(taskName, taskDetails)
            return Response.ok().build()
        }

        override fun get(data: ContainerRequestContext): Response {

            val json = Klaxon().toJsonString(tasks)
            println(json)
            return Response.ok(json).build()
        }
    })

    server.registerCommand("tasks/{task-name}", object : ResourceCallback {
        override fun post(data: ContainerRequestContext): Response {
            return Response.ok().build()
        }

        override fun get(data: ContainerRequestContext): Response {
            val taskName = HTTPCommandUtil().getPathParams(data).getFirst("task-name")
            if (tasks.get(taskName)!!.isBlank())
                return Response.noContent().build()

            val json = Klaxon().toJsonString(tasks.get(taskName))

            return Response.ok(json).build()
        }
    })

    server.registerCommand("date", object : ResourceCallback {
        override fun post(data: ContainerRequestContext): Response {
            return Response.ok().build()
        }

        override fun get(data: ContainerRequestContext): Response {
            return Response.ok(LocalDateTime.now()).build()
        }
    })


    readLine()
}