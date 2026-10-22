package com.juanpablo0612.carpool.data.payment.datasource

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.saveImageToGallery
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess

/** Fetches a remote image and saves it to the device's photo gallery. */
class GalleryDataSource(private val httpClient: HttpClient) {

    suspend fun saveRemoteImage(url: String, fileName: String) {
        val response = httpClient.get(url)
        check(response.status.isSuccess()) { "Image download failed: ${response.status}" }
        FileKit.saveImageToGallery(bytes = response.readRawBytes(), filename = fileName).getOrThrow()
    }
}
