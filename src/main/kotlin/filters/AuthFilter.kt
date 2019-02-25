package me.meegan.rest.filters

import com.sun.xml.internal.messaging.saaj.util.Base64

import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import java.io.IOException

class AuthFilter : ContainerRequestFilter {

    @Throws(IOException::class)
    override fun filter(containerRequestContext: ContainerRequestContext) {
        // Exception thrown if user is unauthorized.
        val unauthorized = WebApplicationException(
            Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"realm\"")
                .entity("Page requires login.").build()
        )

        // Get the authentication passed in HTTP headers parameters
        var auth: String? = containerRequestContext.getHeaderString("authorization") ?: throw unauthorized

        auth = auth!!.replaceFirst("[Bb]asic ".toRegex(), "")
        val userColonPass = Base64.base64Decode(auth)

        if (userColonPass != "$AUTH_USERNAME:$AUTH_PASSWORD")
            throw unauthorized
    }

    companion object {

        private var AUTH_USERNAME = "fiverp-api"
        private var AUTH_PASSWORD = "14892b509360ea539d1c97612e24c261"

        fun setAuthLogin(username: String, password: String) {
            AUTH_USERNAME = username
            AUTH_PASSWORD = password
        }
    }
}