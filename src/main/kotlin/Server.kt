package me.meegan.rest

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import me.meegan.rest.plugin.PluginLoader
import me.meegan.rest.utils.HTTPCommandUtil
import java.awt.event.KeyEvent
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response


fun main() {
    val resources = ResourceList()
    val server = HTTPServerHandler()
    val plugins = PluginLoader

    server.run {
        registerCommand("resources", object : ResourceCallback {
            override fun post(data: ContainerRequestContext): Response {
                val request = HTTPCommandUtil().getBodyJSON(data)

                val resourceName : String = request["name"].toString()
                val resourceDetails : String = request["details"].toString()
                val getResource: JsonObject = request["getResource"] as JsonObject
                val postResource: JsonObject = request["postResource"] as JsonObject
                val putResource: JsonObject = request["putResource"] as JsonObject
                val patchResource: JsonObject = request["patchResource"] as JsonObject
                val deleteResource: JsonObject = request["deleteResource"] as JsonObject

                val getPlugin = plugins.newPluginFromRequest(getResource)

                val postPlugin = plugins.newPluginFromRequest(postResource)

                val putPlugin = plugins.newPluginFromRequest(putResource)

                val patchPlugin = plugins.newPluginFromRequest(patchResource)

                val deletePlugin = plugins.newPluginFromRequest(deleteResource)

                resources.addResource(Resource(resourceName, resourceDetails, getPlugin, postPlugin, putPlugin, deletePlugin, patchPlugin))

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

                return Response.ok(resources[taskID.toInt()]).build()
            }
            override fun delete(data: ContainerRequestContext): Response {
                val id = HTTPCommandUtil().getPathParams(data).getFirst("resource-id")

                resources.removeResource(id.toInt())
                return Response.ok().build()
            }
            override fun put(data: ContainerRequestContext): Response {
                val taskID = HTTPCommandUtil().getPathParams(data).getFirst("resource-id")

                if (taskID.toInt() >= resources.size())
                    return Response.noContent().build()

                val request = HTTPCommandUtil().getBodyJSON(data)

                val resourceName : String = request["name"].toString()
                val resourceDetails : String = request["details"].toString()
                val getResource: JsonObject = request["getResource"] as JsonObject
                val postResource: JsonObject = request["postResource"] as JsonObject
                val putResource: JsonObject = request["putResource"] as JsonObject
                val patchResource: JsonObject = request["patchResource"] as JsonObject
                val deleteResource: JsonObject = request["deleteResource"] as JsonObject

                val getPlugin = plugins.newPluginFromRequest(getResource)

                val postPlugin = plugins.newPluginFromRequest(postResource)

                val putPlugin = plugins.newPluginFromRequest(putResource)

                val patchPlugin = plugins.newPluginFromRequest(patchResource)

                val deletePlugin = plugins.newPluginFromRequest(deleteResource)



                resources.updateResource(taskID.toInt(), Resource(resourceName, resourceDetails, getPlugin, postPlugin, putPlugin, deletePlugin, patchPlugin))

                return Response.ok().build()
            }
        })

        registerCommand("status", object : ResourceCallback {
            override fun get(data: ContainerRequestContext): Response {
                return Response.ok(true).build()
            }
        })

        registerCommand("plugins", object : ResourceCallback {
            override fun get(data: ContainerRequestContext): Response {
                return Response.ok(plugins.getList()).build()
            }
        })

        registerCommand("plugins/{plugin-id}", object : ResourceCallback {
            override fun get(data: ContainerRequestContext): Response {
                val pluginID = HTTPCommandUtil().getPathParams(data).getFirst("plugin-id")

                if (pluginID.toInt() >= resources.size())
                    return Response.noContent().build()

                return Response.ok(plugins.getPlugin(pluginID.toInt())).build()
            }
        })
    }

    resources.addResource(Resource("hi", "prints out hi", "\"hi\""))
    resources.addResource(Resource("bye", "prints out bye", "\"bye\""))
    resources.addResource(Resource("plus/{number1}/{number2}", "prints out 5+5", plugins.newPlugin("addition")))

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
            "keyboard",
            ("key" to KeyEvent.VK_RIGHT),
            ("success" to "You have moved forwards!")
        ),
        plugins.newPlugin(
            "keyboard",
            ("key" to KeyEvent.VK_LEFT),
            ("success" to "You have moved backwards!")
        )
    ))

    resources.addResource(Resource(
        "back",
        "presses left on host pc",
        plugins.newPlugin(
            "keyboard",
            ("key" to KeyEvent.VK_LEFT),
            ("success" to "You have moved backwards!")
        )
    ))

    readLine()
}