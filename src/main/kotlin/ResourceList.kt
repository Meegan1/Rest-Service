package me.meegan.rest

import com.beust.klaxon.Klaxon
import me.meegan.rest.plugin.Plugin
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response

class ResourceList {
    val resources = mutableListOf<Resource>()
    val server = HTTPServerHandler(8088)

    operator fun get(i: Int): Resource = resources[i]

    fun getAll (): MutableList<Resource> = resources

    fun size(): Int = resources.size

    fun addResource(resource: Resource) {
        val index = resources.binarySearch { String.CASE_INSENSITIVE_ORDER.compare(it.name, resource.name) }

        if(index >= 0) {
            resources[index].name = resource.name
            resources[index].details = resource.details
            resources[index].get = resource.get
        }
        else {
            resources.add(-(index + 1), resource)
            server.registerCommand(
                resource.name,
                object: ResourceCallback {
                    override fun post(data: ContainerRequestContext): Response {
                        return Response.ok().build()
                    }

                    override fun get(data: ContainerRequestContext): Response {
                        val script: Any = try {
                                resource.get.run(data)
                             }
                        catch (e: Exception) {
                            e.printStackTrace()
                            return Response.serverError().entity(e.message).build() // Returns the error as a response
                        }
                        val response = Klaxon().toJsonString(script)
                        return Response.ok(response).build()
                    }

                }
            )
        }

    }

    fun removeResource(id: Int) {
        val name = resources[id].name
        resources.removeAt(id)
        server.removeCommand(name)
    }

    fun removeResource(name: String) {
        val index = resources.binarySearch { String.CASE_INSENSITIVE_ORDER.compare(it.name, name) }

        if(index >= 0) {
            removeResource(index)
        }
    }
}


data class Resource(var name: String, var details: String, var get: Plugin) {
    constructor(name: String, details: String, get: String) : this(name, details, Plugin("Script", "This is a generic Script", get))
}