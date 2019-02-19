package me.meegan.rest

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.process.Inflector
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.model.Resource
import java.net.URI
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


class HTTPServerHandler {
    private val BASE_URI = URI.create("http://0.0.0.0:8089/api/")
    private var resourceConfig = create()
    private var server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false)

    init {
            this.start()
            Runtime.getRuntime().addShutdownHook(Thread(Runnable { this.server.shutdownNow() }))
    }

    fun start() {
        if (this.server.isStarted) stop()
        server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false)
        this.server.start()
    }

    fun stop() {
        this.server.shutdownNow()
        resourceConfig = ResourceConfig(resourceConfig)
    }

    fun create() : ResourceConfig {
        val resourceBuilder = Resource.builder("hello")
        resourceBuilder.addMethod("GET").handledBy {
            Response.ok("Hello World!").build()
        }

        val noContentResponder = Inflector<ContainerRequestContext, Response> {
            Response.noContent().build()
        }
        resourceBuilder.addMethod("HEAD").handledBy(noContentResponder)
        resourceBuilder.addMethod("OPTIONS").handledBy(noContentResponder)

        return ResourceConfig().registerResources(resourceBuilder.build())
    }

    fun registerCommand(resource : Resource) {
        if(server.isStarted) stop()
        resourceConfig.registerResources(resource)
        start()
    }
}
