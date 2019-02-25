package me.meegan.rest.utils

import org.glassfish.jersey.server.ContainerRequest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.MultivaluedMap


class HTTPCommandUtil {

    fun getPathParams(data : ContainerRequestContext) : MultivaluedMap<String, String> {
        return getPathParams(data, false)
    }

    fun getPathParams(data: ContainerRequestContext, decode: Boolean): MultivaluedMap<String, String> {
        return data.uriInfo.getPathParameters(decode)
    }

    fun getQueryParams(data : ContainerRequestContext) : MultivaluedMap<String, String> {
        return getQueryParams(data, false)
    }

    fun getQueryParams(data: ContainerRequestContext, decode: Boolean): MultivaluedMap<String, String> {
        return data.uriInfo.getQueryParameters(decode)
    }

    fun getHeaderParams(data : ContainerRequestContext) : MultivaluedMap<String, String> {
        return getHeaderParams(data, false)
    }

    fun getHeaderParams(data: ContainerRequestContext, decode: Boolean): MultivaluedMap<String, String> {
        return data.headers
    }

    /**
     * Parses the body of the ContainerRequestContext argument as a String.
     * @param data ContainerRequestContext
     * @return  String of POST data from ContainerRequestContext
     */
    fun getBodyString(data: ContainerRequestContext): String {
        val cr = data as ContainerRequest
        cr.bufferEntity()
        return cr.readEntity(String::class.java)
    }

}