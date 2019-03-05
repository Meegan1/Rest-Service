package me.meegan.rest

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import java.net.URI
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger


class HTTPServerHandler {
    private val BASE_URI = URI.create("http://0.0.0.0:8089/api/")
    private var resourceConfig : ResourceConfigBuilder = ResourceConfigBuilder()
    private var server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false)

    init {
            this.start()
            Runtime.getRuntime().addShutdownHook(Thread(Runnable { this.server.shutdownNow() }))

            val l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler")
            l.setLevel(Level.CONFIG)
            l.setUseParentHandlers(false)
            val ch = ConsoleHandler()
            ch.level = Level.ALL
            l.addHandler(ch)
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
        if(this.isStarted()) stop()
        resourceConfig.registerResource(label, callback)
        start()
    }

    fun isStarted() : Boolean {
        return this.server.isStarted
    }
}
