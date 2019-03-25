package me.meegan.rest.plugin

import de.swirtz.ktsrunner.objectloader.KtsObjectLoader
import javax.ws.rs.container.ContainerRequestContext

class Plugin(val name: String, val details: String, val script: String, val scriptHeader: String = "", vararg params: Parameter) {
    private var args = mapOf<String, Any>()

    fun run(data: ContainerRequestContext) : Any {
        val engine = KtsObjectLoader()
        engine.engine.put("data", data)
        engine.engine.put("params", args)
        return engine.load(getScriptHead() + script)
    }


    private fun getScriptHead() : String = """
        import me.meegan.rest.utils.HTTPCommandUtil
        import javax.ws.rs.container.ContainerRequestContext
        $scriptHeader
        val data = bindings["data"] as ContainerRequestContext
        val params = bindings["params"] as Map<String, Any>
        """

    fun withParams(vararg params: Pair<String, Any>): Plugin {
        this.args = params.toMap()
        return this
    }

}

data class Parameter(val name: String, val details: String)