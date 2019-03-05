package me.meegan.rest

import com.beust.klaxon.Klaxon
import me.meegan.rest.utils.HTTPCommandUtil
import java.time.LocalDateTime
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


fun main() {

    data class Resource(val name: String, val details: String, val script: String)
    val resources = mutableListOf<Resource>()
    val server = HTTPServerHandler()

    fun MutableList<Resource>.addResource(resource: Resource) {
        val index = resources.binarySearch { String.CASE_INSENSITIVE_ORDER.compare(it.name, resource.name) }

        if(index >= 0)
            this[index] = resource
        else
            this.add(-(index+1), resource)
    }

    server.registerCommand("hi", object: ResourceCallback {
        override fun get(data: ContainerRequestContext): Response {
            return Response.ok("Hello World!").build()
        }

        override fun post(data: ContainerRequestContext): Response {
            return Response.ok().build()
        }
    })

    server.registerCommand("resources", object : ResourceCallback {
        override fun post(data: ContainerRequestContext): Response {
            val resourceName : String = HTTPCommandUtil().getBodyJSON(data).get("resourceName").toString()
            val resourceDetails : String = HTTPCommandUtil().getBodyJSON(data).get("resourceDetails").toString()
            val resourceScript : String = HTTPCommandUtil().getBodyJSON(data).get("resourceScript").toString()

            resources.addResource(Resource(resourceName, resourceDetails, resourceScript))

            return Response.ok().build()
        }

        override fun get(data: ContainerRequestContext): Response {
            return Response.ok(resources).build()
        }
    })

    server.registerCommand("resources/{resource-id}", object : ResourceCallback {
        override fun post(data: ContainerRequestContext): Response {
            return Response.ok().build()
        }

        override fun get(data: ContainerRequestContext): Response {
            val taskID = HTTPCommandUtil().getPathParams(data).getFirst("resource-id")


            if (taskID.toInt() >= resources.size)
                return Response.noContent().build()

            val json = Klaxon().toJsonString(resources[taskID.toInt()])

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

    resources.addResource(Resource("users", "prints out users", "println('user1')"))

    readLine()
}