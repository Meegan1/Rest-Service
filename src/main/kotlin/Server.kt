package me.meegan.rest

import com.beust.klaxon.Klaxon
import me.meegan.rest.plugin.PluginLoader
import me.meegan.rest.utils.HTTPCommandUtil
import java.awt.event.KeyEvent
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


fun main() {
    val resources = ResourceList()
    val server = HTTPServerHandler()
    val plugins = PluginLoader()

    server.run {
        registerCommand("resources", object : ResourceCallback {
            override fun post(data: ContainerRequestContext): Response {
                val resourceName : String = HTTPCommandUtil().getBodyJSON(data)["name"].toString()
                val resourceDetails : String = HTTPCommandUtil().getBodyJSON(data)["details"].toString()
                val resourceGet : String = HTTPCommandUtil().getBodyJSON(data)["get"].toString()

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
    }

    resources.addResource(Resource("hi", "prints out hi", "\"hi\""))
    resources.addResource(Resource("bye", "prints out bye", "\"bye\""))
    resources.addResource(Resource("plus/{number1}/{number2}", "prints out 5+5", plugins.getPlugin("Addition")))

    resources.addResource(Resource("file", "prints out README.md",
            "import java.io.File\n" +
                    "var file = File(\"README.md\")\n" +
                    "var fileName = file.name\n" +
                    "var fileText = file.readText()\n" +
                    "\n" +
                    "data class FileResource(var name: String, var contents: String)\n" +
                    "\n" +
                    "var list = mutableListOf<FileResource>()\n" +
                    "list.add(FileResource(fileName, fileText))\n" +
                    "list"
    ))

    resources.addResource(Resource(
        "forward",
        "presses right on host pc",
        plugins.newPlugin(
            "Keyboard",
            ("key" to KeyEvent.VK_RIGHT),
            ("success" to "You have moved forwards!")
        )
    ))

    resources.addResource(Resource(
        "back",
        "presses left on host pc",
        plugins.newPlugin(
            "Keyboard",
            ("key" to KeyEvent.VK_LEFT),
            ("success" to "You have moved backwards!")
        )
    ))

    readLine()
}