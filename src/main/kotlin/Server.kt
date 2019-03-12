package me.meegan.rest

import com.beust.klaxon.Klaxon
import me.meegan.rest.utils.HTTPCommandUtil
import java.time.LocalDateTime
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


fun main() {
    val resources = ResourceList()
    val server = HTTPServerHandler()

    server.run {
        registerCommand("resources", object : ResourceCallback {
            override fun post(data: ContainerRequestContext): Response {
                val resourceName : String = HTTPCommandUtil().getBodyJSON(data).get("name").toString()
                val resourceDetails : String = HTTPCommandUtil().getBodyJSON(data).get("details").toString()
                val resourceGet : String = HTTPCommandUtil().getBodyJSON(data).get("get").toString()

                resources.addResource(Resource(resourceName, resourceDetails, resourceGet))

                return Response.ok().build()
            }

            override fun get(data: ContainerRequestContext): Response {
                return Response.ok(resources.getAll()).build()
            }
        })

        registerCommand("resources/{resource-id}", object : ResourceCallback {
            override fun get(data: ContainerRequestContext): Response {
                val taskID = HTTPCommandUtil().getPathParams(data).getFirst("resource-id")

                if (taskID.toInt() >= resources.size())
                    return Response.noContent().build()

                val json = Klaxon().toJsonString(resources[taskID.toInt()])

                return Response.ok(json).build()
            }
            override fun delete(data: ContainerRequestContext): Response {
                val id = HTTPCommandUtil().getPathParams(data).getFirst("resource-id")

                resources.removeResource(id.toInt())
                return Response.ok().build()
            }
        })

        registerCommand("date", object : ResourceCallback {
            override fun get(data: ContainerRequestContext): Response {
                return Response.ok(LocalDateTime.now()).build()
            }
        })
    }

    resources.addResource(Resource("hi", "prints out hi", "\"hi\""))
    resources.addResource(Resource("bye", "prints out bye", "\"bye\""))

    readLine()
}