import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.MethodOrderer.Random
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@TestMethodOrder(Random::class)
@Execution(ExecutionMode.CONCURRENT)
class LockControllerShould {

    val dbConnection: IMassiveDbConnection = mockk()
    val path = mockk<MassiveLocation>()
    val mObjectId = "Id1"
    val fullMObject = mockk<IMassiveObject>()
    val wangleLocation = mockk<MassiveLocation>()
    val subscription = mockk<Subscription>(relaxed = true)

    @Test
    fun `lock successfully with these interactions`() {
        every { dbConnection.getPathTo(mObjectId) }.returns(path)
        every { dbConnection.fullObject(path) }.returns(fullMObject)
        every { dbConnection.wangle(fullMObject) }.returns(wangleLocation)
        every { dbConnection.callMassiveMethod(path, wangleLocation, any(), any(), any()) }.returns(0)

        LockController(dbConnection, subscription).lockObject(mObjectId)

        verify { dbConnection.getPathTo(mObjectId) }
        verify { dbConnection.fullObject(path) }
        verify { dbConnection.callMassiveMethod(path, wangleLocation, any(), any(), any()) }
        verify { dbConnection.wangle(fullMObject) }
    }

    @Test
    fun `unlock successfully with these interactions`() {
        every { dbConnection.getPathTo(mObjectId) }.returns(path)
        every { dbConnection.fullObject(path) }.returns(fullMObject)
        every { dbConnection.wangle(fullMObject) }.returns(wangleLocation)
        every { dbConnection.callMassiveMethod(path, wangleLocation, any(), any(), any()) }.returns(0)

        LockController(dbConnection, subscription).unlockObject(mObjectId)

        verify { dbConnection.getPathTo(mObjectId) }
        verify { dbConnection.fullObject(path) }
        verify { dbConnection.callMassiveMethod(path, wangleLocation, any(), any(), any()) }
        verify { dbConnection.wangle(fullMObject) }
    }

    @Test
    fun `return true on lockObject if status is 0 and errors is empty`() {
        every { dbConnection.getPathTo(mObjectId) }.returns(path)
        every { dbConnection.fullObject(path) }.returns(fullMObject)
        every { dbConnection.wangle(fullMObject) }.returns(wangleLocation)
        every { dbConnection.callMassiveMethod(path, wangleLocation, any(), any(), any()) }.returns(0)

        LockController(dbConnection, subscription).lockObject(mObjectId) shouldBe true
    }

    @Test
    fun `return false on lockObject if status is 2`() {
        every { dbConnection.getPathTo(mObjectId) }.returns(path)
        every { dbConnection.fullObject(path) }.returns(fullMObject)
        every { dbConnection.wangle(fullMObject) }.returns(wangleLocation)
        every { dbConnection.callMassiveMethod(path, wangleLocation, any(), any(), any()) }.returns(2)

        LockController(dbConnection, subscription).lockObject(mObjectId) shouldBe false
    }

    @Test
    fun `return false on lockObject if errors occur`() {
        every { dbConnection.getPathTo(mObjectId) }.returns(path)
        every { dbConnection.fullObject(path) }.returns(fullMObject)
        every { dbConnection.wangle(fullMObject) }.returns(wangleLocation)
        var slot = slot<MutableList<String>>()
        every { dbConnection.callMassiveMethod(path, wangleLocation, any(), any(), capture(slot)) }.answers {
            slot.captured.add("Error")
            0
        }

        LockController(dbConnection, subscription).lockObject(mObjectId) shouldBe false
    }
}
