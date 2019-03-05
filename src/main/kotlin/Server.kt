package me.meegan.rest

import com.beust.klaxon.Klaxon
import de.swirtz.ktsrunner.objectloader.KtsObjectLoader
import me.meegan.rest.utils.HTTPCommandUtil
import org.glassfish.hk2.api.MultiException
import java.lang.Exception
import java.lang.IllegalStateException
import java.time.LocalDateTime
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


fun main() {

    data class Resource(var name: String, var details: String, var get: String)
    val resources = mutableListOf<Resource>()
    val server = HTTPServerHandler()

    fun MutableList<Resource>.addResource(resource: Resource) {
        val index = resources.binarySearch { String.CASE_INSENSITIVE_ORDER.compare(it.name, resource.name) }

        if(index >= 0) {
            this[index].name = resource.name
            this[index].details = resource.details
            this[index].get = resource.get
        }
        else {
            this.add(-(index + 1), resource)
            server.registerCommand(
                resource.name,
                object: ResourceCallback {
                    override fun post(data: ContainerRequestContext): Response {
                        return Response.ok().build()
                    }

                    override fun get(data: ContainerRequestContext): Response {
                        val script: String = try {KtsObjectLoader().load(resource.get) }
                        catch (e: Exception) {
                            return Response.serverError().entity(e.message).build()
                        }
                        val response = Klaxon().toJsonString(script)
                        return Response.ok(response).build()
                    }

                }
            )
        }

    }

    server.run {
        registerCommand("resources", object : ResourceCallback {
            override fun post(data: ContainerRequestContext): Response {
                val resourceName : String = HTTPCommandUtil().getBodyJSON(data).get("resourceName").toString()
                val resourceDetails : String = HTTPCommandUtil().getBodyJSON(data).get("resourceDetails").toString()
                val resourceGet : String = HTTPCommandUtil().getBodyJSON(data).get("resourceGet").toString()

                try {
                    resources.addResource(Resource(resourceName, resourceDetails, resourceGet))
                }
                catch (e : Exception) {

                }

                return Response.ok().build()
            }

            override fun get(data: ContainerRequestContext): Response {
                return Response.ok(resources).build()
            }
        })

        registerCommand("resources/{resource-id}", object : ResourceCallback {
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

        registerCommand("date", object : ResourceCallback {
            override fun post(data: ContainerRequestContext): Response {
                return Response.ok().build()
            }

            override fun get(data: ContainerRequestContext): Response {
                return Response.ok(LocalDateTime.now()).build()
            }
        })
    }

    resources.addResource(Resource("hi", "prints out hi", "\"hi\""))
    resources.addResource(Resource("bye", "prints out bye", "\"bye\""))

    readLine()
}