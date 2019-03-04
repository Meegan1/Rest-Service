package me.meegan.rest

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import java.net.URI


class HTTPServerHandler {
    private val BASE_URI = URI.create("http://0.0.0.0:8089/api/")
    private var resourceConfig : ResourceConfigBuilder = ResourceConfigBuilder()
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
        resourceConfig = ResourceConfigBuilder(resourceConfig)
    }

    fun registerCommand(label: String, callback: ResourceCallback) {
        if(server.isStarted) stop()
        resourceConfig.registerResource(label, callback)
        start()
    }

    fun isStarted() : Boolean {
        return this.server.isStarted
    }
}
