package me.meegan.rest.plugin

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import java.io.File

class PluginLoader {
    private val plugins = mutableListOf<Plugin>()
    private val directory = File("plugins")

    init {
        directory.mkdirs()
        reload()
    }

    fun reload() = directory.walk().forEach {
        if(it.isDirectory) {
            val manifest = File(it.absolutePath + "/plugin.manifest")
            val script = File(it.absolutePath + "/script.kts")
            val scriptHeader = File(it.absolutePath + "/script-header.kts")

            if(manifest.exists() && script.exists()) {
                val json = Parser.default().parse(manifest.inputStream()) as JsonObject
                plugins.add(Plugin(
                    json["name"] as String,
                    json["details"] as String,
                    script.readText(),
                    if (scriptHeader.exists()) scriptHeader.readText() else ""
                ))
            }
        }
    }

    fun getPlugin(name: String): Plugin {
        return plugins.find { it.name == name }!!
    }

    fun newPlugin(name : String, vararg params : Pair<String, Any>) : Plugin {
        var plugin = getPlugin(name)
        return Plugin(plugin.name, plugin.details, plugin.script, plugin.scriptHeader).withParams(*params)
    }

    fun getList(): MutableList<Plugin> {
        return this.plugins
    }
}