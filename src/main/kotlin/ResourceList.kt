package me.meegan.rest

import com.beust.klaxon.Klaxon
import me.meegan.rest.plugin.NullPlugin
import me.meegan.rest.plugin.Plugin
import me.meegan.rest.plugin.Script
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
            updateResource(index, resource)
        }
        else {
            resources.add(-(index + 1), resource)
            server.registerCommand(
                resource.name,
                object: ResourceCallback {
                    override fun get(data: ContainerRequestContext): Response {
                        val script: Any = try {
                                resource.getResource.run(data)
                             }
                        catch (e: Exception) {
                            e.printStackTrace()
                            return Response.serverError().entity(e.message).build() // Returns the error as a response
                        }
                        val response = Klaxon().toJsonString(script)
                        return Response.ok(response).build()
                    }

                    override fun post(data: ContainerRequestContext): Response {
                        val script: Any = try {
                            resource.postResource.run(data)
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                            return Response.serverError().entity(e.message).build() // Returns the error as a response
                        }
                        val response = Klaxon().toJsonString(script)
                        return Response.ok(response).build()
                    }

                    override fun put(data: ContainerRequestContext): Response {
                        val script: Any = try {
                            resource.putResource.run(data)
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                            return Response.serverError().entity(e.message).build() // Returns the error as a response
                        }
                        val response = Klaxon().toJsonString(script)
                        return Response.ok(response).build()
                    }

                    override fun delete(data: ContainerRequestContext): Response {
                        val script: Any = try {
                            resource.deleteResource.run(data)
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                            return Response.serverError().entity(e.message).build() // Returns the error as a response
                        }
                        val response = Klaxon().toJsonString(script)
                        return Response.ok(response).build()
                    }

                    override fun patch(data: ContainerRequestContext): Response {
                        val script: Any = try {
                            resource.patchResource.run(data)
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

    fun updateResource(id: Int, resource: Resource) {
        resources.set(id, resource)
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


data class Resource(var name: String, var details: String, var getResource: Plugin = NullPlugin(), var postResource: Plugin = NullPlugin(), var putResource: Plugin = NullPlugin(), var deleteResource: Plugin = NullPlugin(), var patchResource: Plugin = NullPlugin()) {
    constructor(name: String, details: String, get: String) : this(name, details, Script(get))
}