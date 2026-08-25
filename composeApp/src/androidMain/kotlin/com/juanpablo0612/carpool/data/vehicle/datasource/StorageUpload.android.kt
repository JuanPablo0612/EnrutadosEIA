package com.juanpablo0612.carpool.data.vehicle.datasource

import dev.gitlive.firebase.storage.Data
import dev.gitlive.firebase.storage.StorageReference

actual suspend fun StorageReference.upload(data: ByteArray) {
    putData(Data(data))
}
