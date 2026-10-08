package com.sdevprem.common

import androidx.compose.remote.core.CoreDocument
import androidx.compose.remote.core.operations.Header
import androidx.compose.remote.creation.JvmRcPlatformServices
import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.dsl.RcImage
import androidx.compose.remote.creation.dsl.RcProfile
import androidx.compose.remote.creation.dsl.RcScope
import androidx.compose.remote.creation.dsl.createRcBuffer
import androidx.compose.remote.creation.profile.Profile
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

fun prepareDoc(width: Int, height: Int, density: Float, content: RcScope.() -> Unit): ByteArray {
    val profile = Profile(
        CoreDocument.DOCUMENT_API_LEVEL,
        0,
        JvmRcPlatformServices(), //todo: explore
    ) { info, profile, _ ->
        val writer = RemoteComposeWriter(
            profile,
            RemoteComposeWriter.hTag(Header.DOC_WIDTH, info.width),
            RemoteComposeWriter.hTag(Header.DOC_HEIGHT, info.height),
            RemoteComposeWriter.hTag(Header.DOC_DENSITY_AT_GENERATION, density),
            RemoteComposeWriter.hTag(Header.DOC_CONTENT_DESCRIPTION, ""),
            RemoteComposeWriter.hTag(Header.DOC_PROFILES, 0),
        )
        return@Profile writer
    }

    return createRcBuffer(
        RcProfile(profile),
        RemoteComposeWriter.hTag(Header.DOC_WIDTH, width),
        RemoteComposeWriter.hTag(Header.DOC_HEIGHT, height),
        RemoteComposeWriter.hTag(Header.DOC_CONTENT_DESCRIPTION, ""),
        RemoteComposeWriter.hTag(Header.DOC_PROFILES, 0),
        RemoteComposeWriter.hTag(Header.DOC_DENSITY_AT_GENERATION, density),
        content = content
    )
}
