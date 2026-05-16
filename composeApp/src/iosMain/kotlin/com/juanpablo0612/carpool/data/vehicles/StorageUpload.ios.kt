package com.juanpablo0612.carpool.data.vehicles

import dev.gitlive.firebase.storage.StorageReference

actual suspend fun StorageReference.upload(data: ByteArray) {
    error("Storage upload not implemented for iOS")
}
