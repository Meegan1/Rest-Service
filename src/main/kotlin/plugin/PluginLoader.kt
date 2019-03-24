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
        directory.walk().forEach {
            if(it.isDirectory) {
                val manifest = File(it.absolutePath + "/plugin.manifest")
                val script = File(it.absolutePath + "/script.kts")

                if(manifest.exists() && script.exists()) {
                    val json = Parser.default().parse(manifest.inputStream()) as JsonObject
                    if(!json.containsKey("params"))
                        plugins.add(Plugin(json!!["name"] as String, json["details"] as String, script.readText()))
                    else
                        plugins.add(Plugin(json!!["name"] as String, json["details"] as String, script.readText(),
                            json["params"] as List<Parameter>?
                        ))
                }
            }
        }
    }

    fun reload() = directory.walk().forEach {
        if(it.isDirectory) {
            val manifest = File(it.absolutePath + "plugin" + ".manifest")
            val script = File(it.absolutePath + "script.kts")

            if(manifest.exists() && script.exists()) {
                val json = Klaxon().parse<JsonObject>(manifest)
                plugins.add(Plugin(json!!["name"] as String, json["details"] as String, script.readText()))
            }
        }
    }

    fun getPluginScript(name: String): String {
        return plugins.find { it.name.equals(name) }!!.script
    }
}