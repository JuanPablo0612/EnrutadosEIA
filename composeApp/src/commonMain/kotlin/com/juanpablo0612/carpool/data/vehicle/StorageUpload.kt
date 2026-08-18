package com.juanpablo0612.carpool.data.vehicle

import dev.gitlive.firebase.storage.StorageReference

expect suspend fun StorageReference.upload(data: ByteArray)
