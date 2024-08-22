package pt.andremartins.workshop.io

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

data class User(val id: Int, val name: String)

class UserRepo(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val users = mutableListOf<User>()

    suspend fun addUser(user: User) {
        delay(200)
        users.add(user)
    }

    suspend fun getUsers(): List<User> {
        delay(500)
        return users.toList()
    }

    fun launchBackgroundJob() {
        CoroutineScope(dispatcher).launch {
            addUser(User(50, "John"))
            addUser(User(60, "Doe"))
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTest {

    @Test
    fun foo() = runTest {
        val userRepo = UserRepo()
        // Scheduled on Test Dispatcher
        launch { userRepo.addUser(User(1, "André")) }
        launch { userRepo.addUser(User(2, "Miguel")) }

        runCurrent()
        advanceTimeBy(2_000)
        advanceUntilIdle()

        assertEquals(
            listOf(User(1, "André"), User(2, "Miguel")),
            userRepo.getUsers()
        )
    }

    @Test
    fun bar() = runTest {
        val userRepo = UserRepo()

        userRepo.launchBackgroundJob()
        advanceUntilIdle() // does not work

        assertEquals(
            listOf(User(50, "John"), User(60, "Doe")),
            userRepo.getUsers()
        )
    }

    @Test
    fun barCorrect() = runTest {
        val userRepo = UserRepo(StandardTestDispatcher(testScheduler))

        userRepo.launchBackgroundJob()
        advanceUntilIdle()

        assertEquals(
            listOf(User(50, "John"), User(60, "Doe")),
            userRepo.getUsers()
        )
    }
}
