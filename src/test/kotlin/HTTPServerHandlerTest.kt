import me.meegan.rest.HTTPServerHandler
import org.glassfish.jersey.server.model.Resource
import org.junit.jupiter.api.Assertions.assertNotNull
import javax.ws.rs.core.Response

internal class HTTPServerHandlerTest {

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
    }

    @org.junit.jupiter.api.Test
    fun start() {
        val server = HTTPServerHandler()
        val resourceBuilder = Resource.builder("hi")
        resourceBuilder.addMethod("GET").handledBy {
            Response.ok("Hi World!").build()
        }

        server.registerCommand(resourceBuilder.build())

        assertNotNull(server)
    }
}