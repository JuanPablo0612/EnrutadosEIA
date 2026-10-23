package com.juanpablo0612.carpool.data.notification.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.notification.datasource.NotificationRemoteDataSource
import com.juanpablo0612.carpool.data.notification.model.NotificationDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NotificationRepositoryImplTest {

    private class FakeDataSource(private val unread: Flow<Int>) : NotificationRemoteDataSource {
        override fun getNotifications(userId: String): Flow<List<NotificationDto>> = emptyFlow()
        override fun observeUnreadCount(userId: String): Flow<Int> = unread
        override suspend fun markRead(userId: String, notificationId: String) = Unit
        override suspend fun delete(userId: String, notificationId: String) = Unit
        override suspend fun clearAll(userId: String) = Unit
    }

    @Test
    fun unreadCountPassesThroughEachUpdate() = runTest {
        val repository = NotificationRepositoryImpl(FakeDataSource(flowOf(0, 2, 1)))

        assertEquals(listOf(0, 2, 1), repository.observeUnreadCount("u1").toList())
    }

    @Test
    fun unreadCountFailureIsMappedToAppException() = runTest {
        val failing = flow {
            emit(3)
            throw IllegalStateException("PERMISSION_DENIED")
        }
        val repository = NotificationRepositoryImpl(FakeDataSource(failing))

        assertFailsWith<AppException.NotificationException.Unknown> {
            repository.observeUnreadCount("u1").toList()
        }
    }
}
