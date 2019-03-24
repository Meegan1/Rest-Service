package me.meegan.rest.plugin

class Plugin(val name: String, val details: String, val script: String, val params: List<Parameter>?) {
    constructor(name: String, details: String, script: String) : this(name, details, script, null)
}

data class Parameter(val name: String, val details: String)