package net.ankio.webdav.lib.engine

import net.ankio.webdav.lib.WebDavResource
import java.io.InputStream

/**
 * WebDAV 底层能力抽象，便于替换 Sardine 等实现。
 */
interface WebDavEngine {
    fun exists(path: String): Boolean

    fun list(path: String = ""): List<WebDavResource>

    fun mkdir(path: String)

    fun delete(path: String)

    fun read(path: String): InputStream

    fun write(path: String, data: ByteArray, contentType: String? = null)

    fun move(sourcePath: String, destinationPath: String, overwrite: Boolean = true)

    fun copy(sourcePath: String, destinationPath: String)
}
