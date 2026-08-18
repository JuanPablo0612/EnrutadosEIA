package com.juanpablo0612.carpool.data.vehicle.datasource

import dev.gitlive.firebase.storage.Data
import dev.gitlive.firebase.storage.StorageReference
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import platform.Foundation.NSData
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class)
actual suspend fun StorageReference.upload(data: ByteArray) {
    val nsData = memScoped {
        NSData.create(bytes = allocArrayOf(data), length = data.size.toULong())
    }
    putData(Data(nsData))
}
