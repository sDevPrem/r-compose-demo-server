package com.sdevprem

import androidx.compose.remote.core.CoreDocument
import androidx.compose.remote.core.operations.Header
import androidx.compose.remote.core.operations.layout.managers.ColumnLayout
import androidx.compose.remote.core.operations.layout.managers.CoreText
import androidx.compose.remote.creation.JvmRcPlatformServices
import androidx.compose.remote.creation.Rc
import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.dsl.Modifier
import androidx.compose.remote.creation.dsl.RcColumnVerticalPositioning
import androidx.compose.remote.creation.dsl.RcContentScale
import androidx.compose.remote.creation.dsl.RcHorizontalPositioning
import androidx.compose.remote.creation.dsl.RcImage
import androidx.compose.remote.creation.dsl.RcProfile
import androidx.compose.remote.creation.dsl.RcScope
import androidx.compose.remote.creation.dsl.background
import androidx.compose.remote.creation.dsl.createRcBuffer
import androidx.compose.remote.creation.dsl.fillMaxSize
import androidx.compose.remote.creation.dsl.height
import androidx.compose.remote.creation.dsl.padding
import androidx.compose.remote.creation.dsl.rdp
import androidx.compose.remote.creation.dsl.width
import androidx.compose.remote.creation.modifiers.RecordingModifier
import androidx.compose.remote.creation.profile.Profile
import com.sdevprem.common.remoteBitmapUrlWithBounds
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        routing {
            get("/festive") {
                val width = call.request.queryParameters["w"]?.toIntOrNull() ?: 1080
                val height = call.request.queryParameters["h"]?.toIntOrNull() ?: 2400
                val density = call.request.queryParameters["d"]?.toFloatOrNull() ?: 1f

                val bytes = festivePage(width, height, density)
                println("bytes: ${bytes.size}")
                call.respondBytes(
                    bytes = bytes,
                    contentType = ContentType.Application.OctetStream
                )
            }
            get("/clock") {
                val width = call.request.queryParameters["w"]?.toIntOrNull() ?: 1080
                val height = call.request.queryParameters["h"]?.toIntOrNull() ?: 2400
                val density = call.request.queryParameters["d"]?.toFloatOrNull() ?: 1f

                val bytes = _root_ide_package_.com.sdevprem.demo.clock(width, height, density)
                println("bytes: ${bytes.size}")
                call.respondBytes(
                    bytes = bytes,
                    contentType = ContentType.Application.OctetStream
                )
            }
            get("/hosted-image.png") {
                println("DEBUG: Android player requested the image URL!")

                val outputStream = ByteArrayOutputStream()
                ImageIO.write(getBufferedImage(), "png", outputStream)
                call.respondBytes(
                    bytes = outputStream.toByteArray(),
                    contentType = ContentType.Image.PNG
                )
            }
        }
    }.start(wait = true)
}

private fun main3(): ByteArray {
    val platform = JvmRcPlatformServices()
    val density = 2.625f
    val width = (400 * density).toInt()
    val height = (800 * density).toInt()
    val writer = RemoteComposeWriter(
        platform,
        RemoteComposeWriter.HTag(Header.DOC_WIDTH, width),
        RemoteComposeWriter.HTag(Header.DOC_HEIGHT, height),
    )

    writer.root {

        val rootMod = RecordingModifier()
            .fillMaxSize()
            .background(0xFFFF0000.toInt()) // Opaque Red Background

        writer.column(rootMod, ColumnLayout.CENTER, ColumnLayout.CENTER) {

            val textId = writer.addText("HELLO FROM KTOR SERVER!")
//            val imageId = writer.addBitmap(getImageBytes())
            val imageId = writer.addBitmapUrl("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png", 800, 600)

            println("img ID: $imageId")
            println("text ID: $textId")


            writer.textComponent(
                RecordingModifier(),
                textId,
                0xFF000000.toInt(),
                24f * density,
                0,
                400F,
                null,
                CoreText.TEXT_ALIGN_CENTER,
                0,
                0
            ) {}

            writer.image(
                RecordingModifier()
                    .width(200)
                    .height(200),
                imageId,
                Rc.ImageScale.CROP,
                1.0f
            )
        }
    }

    val binaryPayload = writer.encodeToByteArray()
    println("DEBUG: Generated ${binaryPayload.size} bytes")
    return binaryPayload
}

fun main2(width: Int, height: Int, density: Float): ByteArray {
    var writer: RemoteComposeWriter? = null

    val profile = Profile(
        CoreDocument.DOCUMENT_API_LEVEL,
        0,
        JvmRcPlatformServices(), //todo: explore
    ) { info, profile, _ ->
        writer = RemoteComposeWriter(
            profile,
            RemoteComposeWriter.hTag(Header.DOC_WIDTH, info.width),
            RemoteComposeWriter.hTag(Header.DOC_HEIGHT, info.height),
            RemoteComposeWriter.hTag(Header.DOC_DENSITY_AT_GENERATION, density),
            RemoteComposeWriter.hTag(Header.DOC_CONTENT_DESCRIPTION, ""),
            RemoteComposeWriter.hTag(Header.DOC_PROFILES, 0),
        )
        return@Profile writer
    }

    val bytes = createRcBuffer(
        RcProfile(profile),
        RemoteComposeWriter.hTag(Header.DOC_WIDTH, width),
        RemoteComposeWriter.hTag(Header.DOC_HEIGHT, height),
        RemoteComposeWriter.hTag(Header.DOC_CONTENT_DESCRIPTION, ""),
        RemoteComposeWriter.hTag(Header.DOC_PROFILES, 0),
    ) {

        Column(
            modifier = Modifier.fillMaxSize().background(0xFFEEEEEE.toInt()).padding(20f),
            horizontal = RcHorizontalPositioning.Center,
            vertical = RcColumnVerticalPositioning.Center,
        ) {

            Image(
                remoteBitmapUrlWithBounds(url = "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png", 800, 600),
                modifier = Modifier.width(200.rdp).height(200.rdp),
                contentScale = RcContentScale.Crop,

            )
        }
    }
    return bytes
}


fun getBufferedImage(): BufferedImage {
    val imageBytes = runBlocking {
        val client = HttpClient(CIO)
        // Ensure you fetch a valid, direct image file (PNG/JPEG)
        val response =
            client.get("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png")
        print(response.status.value)
        client.close()
        response.readRawBytes()
    }
    val data = ImageIO.read(ByteArrayInputStream(imageBytes))
    println("IMAGE BYTES: $data")
    return data
}

fun getImageBytes(): ByteArray {
    return runBlocking {
        val client = HttpClient(CIO)
        // Ensure you fetch a valid, direct image file (PNG/JPEG)
        val response =
            client.get("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png")
        print(response.status.value)
        client.close()
        response.readRawBytes()
    }
}

fun RcScope.NetworkImage(
    url: String,
    intrinsicWidth: Int,
    intrinsicHeight: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: RcContentScale = RcContentScale.Crop,
    alpha: Float = 1.0f
) {
    try {
        val writerProperty = this::class.memberProperties.find { it.name == "writer" }
            ?: throw IllegalStateException("Could not find 'writer' property in Kotlin reflection")

        writerProperty.isAccessible = true
        val writer = writerProperty.getter.call(this) as RemoteComposeWriter

        val imageId = writer.addBitmapUrl(url, intrinsicWidth, intrinsicHeight)

        val constructor = RcImage::class.constructors.first()
        constructor.isAccessible = true
        val rcImage = constructor.call(imageId)

        this.Image(
            image = rcImage,
            modifier = modifier,
            contentDescription = contentDescription,
            contentScale = contentScale,
            alpha = alpha
        )

    } catch (e: Exception) {
        println("DEBUG: Kotlin Reflection failed: ${e.message}")
        e.printStackTrace()
    }
}
