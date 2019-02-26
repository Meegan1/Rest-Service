package me.meegan.rest

import com.beust.klaxon.Klaxon
import me.meegan.rest.utils.HTTPCommandUtil
import java.time.LocalDateTime
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


fun main() {

    data class Task(val name: String, val details: String, val priority: Int)
    val tasks = mutableListOf<Task>()
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
            val taskName : String = HTTPCommandUtil().getBodyJSON(data).get("taskName").toString()
            val taskDetails : String = HTTPCommandUtil().getBodyJSON(data).get("taskDetails").toString()
            val taskPriority : Int = HTTPCommandUtil().getBodyJSON(data).get("taskPriority") as Int

            val index = tasks.binarySearch { String.CASE_INSENSITIVE_ORDER.compare(it.name, taskName) }

            if(index != -1)
                tasks[index] = Task(taskName, taskDetails, taskPriority)
            else
                tasks.add(Task(taskName, taskDetails, taskPriority))

            return Response.ok().build()
        }

        override fun get(data: ContainerRequestContext): Response {
            return Response.ok(tasks).build()
        }
    })

    server.registerCommand("tasks/{task-id}", object : ResourceCallback {
        override fun post(data: ContainerRequestContext): Response {
            return Response.ok().build()
        }

        override fun get(data: ContainerRequestContext): Response {
            val taskID = HTTPCommandUtil().getPathParams(data).getFirst("task-id")


            if (taskID.toInt() >= tasks.size)
                return Response.noContent().build()

            val json = Klaxon().toJsonString(tasks[taskID.toInt()])

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

    tasks.add(Task("Washing", "I need to do the dishes", 0))
    tasks.add(Task("Tidy", "The house is dirty", 2))
    tasks.add(Task("Cleaning", "The house is dirty", 1))
    tasks.add(Task("Shower", "", 2))

    readLine()
}