import me.meegan.rest.HTTPServerHandler
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import kotlin.test.assertTrue

class HTTPServerHandlerTest {
    val server = HTTPServerHandler()

    @Test
    fun start() {
        server.start()
        assertTrue(server.isStarted())
    }

    @Test
    fun stop() {
        server.stop()
        assertTrue(!server.isStarted())
    }
}