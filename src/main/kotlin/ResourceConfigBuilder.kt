package me.meegan.rest


import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.model.Resource
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

internal class ResourceConfigBuilder : ResourceConfig {

    /**
     * Initializes a new empty ResourceConfig
     */
    constructor() {
        register(me.meegan.rest.filters.CORSFilter())
    }

    /**
     * Initializes a me.meegan.rest.ResourceConfigBuilder from me.meegan.rest.ResourceConfigBuilder argument
     * (use when previous config has been used).
     * @param config
     */
    constructor(config: ResourceConfigBuilder) : super(config)

    /**
     * Registers the resource to the config.
     * @param path
     * @param callback
     */
    fun registerResource(path: String, callback: ResourceCallback) {
        val resourceBuilder = Resource.builder()
        resourceBuilder.path(path)

        resourceBuilder.addMethod("GET").produces(MediaType.APPLICATION_JSON_TYPE)
            .handledBy { data -> callback.get(data) }

        resourceBuilder.addMethod("POST").produces(MediaType.APPLICATION_JSON_TYPE)
            .handledBy { data -> callback.post(data) }

        resourceBuilder.addMethod("PUT").produces(MediaType.APPLICATION_JSON_TYPE)
            .handledBy { data -> callback.put(data) }

        resourceBuilder.addMethod("PATCH").produces(MediaType.APPLICATION_JSON_TYPE)
            .handledBy { data -> callback.patch(data) }

        resourceBuilder.addMethod("DELETE").produces(MediaType.APPLICATION_JSON_TYPE)
            .handledBy { data -> callback.delete(data) }

        val resource = resourceBuilder.build()
        registerResources(resource)
    }

    fun removeResource(path: String) {
        val resourceBuilder = Resource.builder()
        resourceBuilder.path(path)

        val resource = resourceBuilder.build()
        registerResources(resource)
    }

}

interface ResourceCallback {
    fun post(data: ContainerRequestContext): Response = Response.serverError().build()
    fun get(data: ContainerRequestContext): Response = Response.serverError().build()
    fun put(data: ContainerRequestContext): Response = Response.serverError().build()
    fun patch(data: ContainerRequestContext): Response = Response.serverError().build()
    fun delete(data: ContainerRequestContext): Response = Response.serverError().build()
}