package me.meegan.rest

import org.glassfish.jersey.server.model.Resource
import javax.ws.rs.core.Response

fun main(args : Array<String>) {
    val server = HTTPServerHandler()
    val resourceBuilder = Resource.builder("hi")
    resourceBuilder.addMethod("GET").handledBy {
        Response.ok("Hi World!").build()
    }

    server.registerCommand(resourceBuilder.build())
    readLine()
}