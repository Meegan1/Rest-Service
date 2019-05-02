package me.meegan.rest.plugin

import com.beust.klaxon.*
import java.io.File

object PluginLoader {
    private val plugins = mutableListOf<PluginConfig>()
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
                plugins.add(PluginConfig(
                    json["name"] as String,
                    json["details"] as String,
                    script.readText(),
                    if (scriptHeader.exists()) scriptHeader.readText() else "",
                    json["parameters"] as JsonArray<out Any?>
                ))
            }
        }
    }

    fun getPlugin(name: String): PluginConfig? {
        return plugins.find { it.name == name }
    }

    fun getPlugin(id: Int): PluginConfig {
        return plugins[id]
    }

    fun newPlugin(name : String, vararg params : Pair<String, Any>) : Plugin {
        val plugin = getPlugin(name) ?: return NullPlugin()
        return Plugin(plugin.name, *params)
    }

    fun getList(): MutableList<PluginConfig> {
        return this.plugins
    }
}

class PluginConfig(val name: String, val details: String, val script: String, val scriptHeader: String = "", val params: JsonArray<out Any?>)

data class Parameter(val name: String, val details: String)