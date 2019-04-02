import me.meegan.rest.Resource
import me.meegan.rest.ResourceList
import org.junit.AfterClass
import org.junit.Test

import org.junit.Assert.*
import org.junit.BeforeClass

class ResourceListTest {
    companion object {
            init {
                // things that may need to be setup before companion class member variables are instantiated
            }

        // variables you initialize for the class just once:
        val resources = ResourceList()

        @BeforeClass @JvmStatic fun setup() {
            // things to execute once and keep around for the class
            resources.addResource(Resource("test1", "test resource 1", "\"first test resource\""))
            resources.addResource(Resource("test2", "test resource 2", "\"second test resource\""))

        }

        @AfterClass @JvmStatic fun teardown() {
            // clean up after this class, leave nothing dirty behind
        }
    }

    @Test
    fun getServer() {

    }

    @Test
    fun get() {
        val myResource = resources.get(0)
        assertEquals("test1", myResource.name)
        assertEquals("test resource 1", myResource.details)
    }

    @Test
    fun getAll() {
        val expectedNameArrayList = arrayListOf<String>()
        expectedNameArrayList.plus("test1")
        expectedNameArrayList.plus("test2")

        val mutableResourceList = resources.getAll()
        var actualNameArrayList = arrayListOf<String>()
        for (item in mutableResourceList) {
            actualNameArrayList.plus(item.name)
        }
        assert(actualNameArrayList.containsAll(expectedNameArrayList))
    }

    @Test
    fun size() {
        resources.addResource(Resource("test3", "test resource 3", "\"third test resource\""))
        resources.addResource(Resource("test4", "test resource 4", "\"fourth test resource\""))
        assertEquals(4, resources.size())
    }

    @Test
    fun addAndRemoveResourceById() {
        resources.addResource((Resource("aaa-test", "test resource", "\"test\"")))
        assertEquals(5, resources.size())
        resources.removeResource(0)
        assertEquals(4, resources.size())
    }

    @Test
    fun addAndRemoveResourceByName() {
        resources.addResource((Resource("name-test", "test resource", "\"test\"")))
        assertEquals(5, resources.size())
        resources.removeResource("name-test")
        assertEquals(4, resources.size())
    }
}