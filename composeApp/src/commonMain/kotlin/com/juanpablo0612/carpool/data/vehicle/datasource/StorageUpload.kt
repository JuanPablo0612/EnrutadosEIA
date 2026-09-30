package com.juanpablo0612.carpool.data.vehicle.datasource

import dev.gitlive.firebase.storage.FirebaseStorageMetadata
import dev.gitlive.firebase.storage.StorageReference
import dev.gitlive.firebase.storage.storageMetadata

/**
 * Uploads JPEG bytes with an explicit content type. Raw bytes carry no type, so without it
 * Storage records `application/octet-stream` and storage.rules rejects the write as not an image.
 */
suspend fun StorageReference.uploadJpeg(data: ByteArray) {
    upload(data, storageMetadata { contentType = "image/jpeg" })
}

internal expect suspend fun StorageReference.upload(data: ByteArray, metadata: FirebaseStorageMetadata)
