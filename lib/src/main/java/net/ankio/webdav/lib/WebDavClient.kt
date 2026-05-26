package net.ankio.webdav.lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ankio.webdav.lib.engine.SardineWebDavEngine
import net.ankio.webdav.lib.engine.WebDavEngine
import java.io.InputStream

/**
 * WebDAV 对外 API：在 IO 线程执行底层引擎调用。
 */
class WebDavClient internal constructor(
    private val config: WebDavConfig,
    private val engine: WebDavEngine,
) {

    val configuration: WebDavConfig
        get() = config

    suspend fun exists(path: String = ""): Boolean = io { engine.exists(path) }

    suspend fun list(path: String = ""): List<WebDavResource> = io { engine.list(path) }

    suspend fun mkdir(path: String) = io { engine.mkdir(path) }

    suspend fun mkdirs(path: String) = io {
        val segments = path.trim('/').split('/').filter { it.isNotEmpty() }
        if (segments.isEmpty()) return@io
        val builder = StringBuilder()
        for (segment in segments) {
            builder.append('/').append(segment)
            val current = builder.toString()
            if (!engine.exists(current)) {
                engine.mkdir(current)
            }
        }
    }

    suspend fun delete(path: String) = io { engine.delete(path) }

    suspend fun readBytes(path: String): ByteArray = io {
        engine.read(path).use { it.readBytes() }
    }

    suspend fun readText(path: String, charset: java.nio.charset.Charset = Charsets.UTF_8): String =
        readBytes(path).toString(charset)

    suspend fun writeBytes(path: String, data: ByteArray, contentType: String? = null) =
        io { engine.write(path, data, contentType) }

    suspend fun writeText(
        path: String,
        text: String,
        charset: java.nio.charset.Charset = Charsets.UTF_8,
        contentType: String = "text/plain; charset=${charset.name()}",
    ) = writeBytes(path, text.toByteArray(charset), contentType)

    suspend fun move(sourcePath: String, destinationPath: String, overwrite: Boolean = true) =
        io { engine.move(sourcePath, destinationPath, overwrite) }

    suspend fun copy(sourcePath: String, destinationPath: String) =
        io { engine.copy(sourcePath, destinationPath) }

    suspend fun open(path: String): InputStream = io { engine.read(path) }

    private suspend fun <T> io(block: suspend () -> T): T = withContext(Dispatchers.IO) { block() }

    companion object {
        fun create(config: WebDavConfig): WebDavClient =
            WebDavClient(config, SardineWebDavEngine(config))
    }
}
