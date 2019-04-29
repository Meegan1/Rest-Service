package me.meegan.rest.plugin

import de.swirtz.ktsrunner.objectloader.KtsObjectLoader
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response

open class Plugin(val name: String, vararg val params: Pair<String, Any>) {

    open fun run(data: ContainerRequestContext) : Any {
        val engine = KtsObjectLoader()
        engine.engine.put("data", data)
        engine.engine.put("params", params.toMap())
        return engine.load(getScriptHead() + PluginLoader.getPlugin(name).script)
    }


    private fun getScriptHead() : String = """
        import me.meegan.rest.utils.HTTPCommandUtil
        import javax.ws.rs.container.ContainerRequestContext
        ${PluginLoader.getPlugin(name).scriptHeader}
        val data = bindings["data"] as ContainerRequestContext
        val params = bindings["params"] as Map<String, Any>
        """
}

open class Script(val script: String) : Plugin("Custom Script") {
    override fun run(data: ContainerRequestContext) : Any {
        val engine = KtsObjectLoader()
        engine.engine.put("data", data)
        engine.engine.put("params", params)
        return engine.load(getScriptHead() + script)
    }

    private fun getScriptHead() : String = """
        import me.meegan.rest.utils.HTTPCommandUtil
        import javax.ws.rs.container.ContainerRequestContext
        val data = bindings["data"] as ContainerRequestContext
        """
}

class NullPlugin : Plugin("Invalid Request") {
    override fun run(data: ContainerRequestContext): Any {
        return Response.noContent()
    }
}
