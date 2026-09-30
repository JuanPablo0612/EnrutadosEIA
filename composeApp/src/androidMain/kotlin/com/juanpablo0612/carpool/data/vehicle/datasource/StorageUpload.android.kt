package com.juanpablo0612.carpool.data.vehicle.datasource

import dev.gitlive.firebase.storage.Data
import dev.gitlive.firebase.storage.FirebaseStorageMetadata
import dev.gitlive.firebase.storage.StorageReference

internal actual suspend fun StorageReference.upload(data: ByteArray, metadata: FirebaseStorageMetadata) {
    putData(Data(data), metadata)
}
