package com.juanpablo0612.carpool.data.notification.repository

import com.juanpablo0612.carpool.core.exception.AppException
import com.juanpablo0612.carpool.data.notification.datasource.NotificationRemoteDataSource
import com.juanpablo0612.carpool.data.notification.model.NotificationDto
import com.juanpablo0612.carpool.domain.notification.model.AppNotification
import com.juanpablo0612.carpool.domain.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val remoteDataSource: NotificationRemoteDataSource
) : NotificationRepository {

    override fun getNotifications(userId: String): Flow<List<AppNotification>> {
        return remoteDataSource.getNotifications(userId)
            .map { list ->
                list.map { it.toDomain() }.sortedByDescending { it.timestamp }
            }
    }

    override suspend fun createNotification(notification: AppNotification): Result<Unit> {
        return try {
            val dto = NotificationDto.fromDomain(notification)
            remoteDataSource.createNotification(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.NotificationException.Unknown)
        }
    }

    override suspend fun markRead(userId: String, notificationId: String): Result<Unit> {
        return try {
            remoteDataSource.markRead(userId, notificationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.NotificationException.Unknown)
        }
    }

    override suspend fun delete(userId: String, notificationId: String): Result<Unit> {
        return try {
            remoteDataSource.delete(userId, notificationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.NotificationException.Unknown)
        }
    }

    override suspend fun clearAll(userId: String): Result<Unit> {
        return try {
            remoteDataSource.clearAll(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppException.NotificationException.Unknown)
        }
    }
}
