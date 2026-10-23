package com.juanpablo0612.carpool.domain.chat.usecase

import com.juanpablo0612.carpool.domain.chat.model.Message
import com.juanpablo0612.carpool.domain.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SendMessageUseCaseTest {

    private class FakeChatRepository(var fail: Boolean = false) : ChatRepository {
        val sent = mutableListOf<Message>()
        override fun getMessages(bookingId: String): Flow<List<Message>> = emptyFlow()
        override suspend fun sendMessage(message: Message): Result<Unit> {
            sent += message
            return if (fail) Result.failure(IllegalStateException()) else Result.success(Unit)
        }
        override suspend fun markMessagesRead(bookingId: String, userId: String): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun createdMessageIsTrimmedUnreadAndStampedWithNow() {
        val message = SendMessageUseCase(FakeChatRepository()).createMessage("b1", "u1", "  hola  ", now = 1_000L)

        assertEquals(Message(id = "1000_u1", bookingId = "b1", senderId = "u1", text = "hola", timestamp = 1_000L, isRead = false), message)
    }

    @Test
    fun retryResendsTheSameMessageSoItCannotDuplicate() = runTest {
        val repository = FakeChatRepository(fail = true)
        val useCase = SendMessageUseCase(repository)
        val message = useCase.createMessage("b1", "u1", "hola", now = 1_000L)

        assertTrue(useCase(message).isFailure)
        repository.fail = false
        assertFalse(useCase(message).isFailure)

        assertEquals(listOf(message, message), repository.sent)
    }
}
