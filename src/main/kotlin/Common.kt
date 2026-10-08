package com.sdevprem

import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.dsl.RcImage
import androidx.compose.remote.creation.dsl.RcScope
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun RcScope.remoteBitmapUrlWithBounds(
    url: String,
    intrinsicWidth: Int,
    intrinsicHeight: Int
): RcImage {
    // 1. Find the Kotlin property named 'writer'
    val writerProperty = this::class.memberProperties.find { it.name == "writer" }
        ?: throw IllegalStateException("Could not find 'writer' property in Kotlin reflection")

    // 2. Unlock the property and grab the instance
    writerProperty.isAccessible = true
    val writer = writerProperty.getter.call(this) as RemoteComposeWriter

    // 3. Register the URL with explicit dimensions into the binary dictionary!
    val imageId = writer.addBitmapUrl(url, intrinsicWidth, intrinsicHeight)

    // 4. Instantiate the RcImage wrapper using Kotlin reflection and return it
    val constructor = RcImage::class.constructors.first()
    constructor.isAccessible = true
    return constructor.call(imageId)
}