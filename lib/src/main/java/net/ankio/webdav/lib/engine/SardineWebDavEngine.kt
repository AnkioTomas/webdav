package net.ankio.webdav.lib.engine

import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import net.ankio.webdav.lib.WebDavConfig
import net.ankio.webdav.lib.WebDavPaths
import net.ankio.webdav.lib.WebDavResource
import java.io.InputStream

/**
 * 基于 [Sardine] 的 WebDAV 实现，第三方依赖仅在此类中出现。
 */
internal class SardineWebDavEngine(
    private val config: WebDavConfig,
    private val sardine: Sardine = OkHttpSardine(),
) : WebDavEngine {

    init {
        sardine.setCredentials(config.username, config.password)
    }

    override fun exists(path: String): Boolean = sardine.exists(resolve(path))

    override fun list(path: String): List<WebDavResource> =
        sardine.list(resolve(path)).map { it.toWebDavResource() }

    override fun mkdir(path: String) {
        sardine.createDirectory(resolve(path))
    }

    override fun delete(path: String) {
        sardine.delete(resolve(path))
    }

    override fun read(path: String): InputStream = sardine.get(resolve(path))

    override fun write(path: String, data: ByteArray, contentType: String?) {
        val url = resolve(path)
        if (contentType == null) {
            sardine.put(url, data)
        } else {
            sardine.put(url, data, contentType)
        }
    }

    override fun move(sourcePath: String, destinationPath: String, overwrite: Boolean) {
        sardine.move(resolve(sourcePath), resolve(destinationPath), overwrite)
    }

    override fun copy(sourcePath: String, destinationPath: String) {
        sardine.copy(resolve(sourcePath), resolve(destinationPath))
    }

    private fun resolve(path: String): String = WebDavPaths.join(config.baseUrl, path)

    private fun DavResource.toWebDavResource(): WebDavResource {
        val hrefPath = href?.path.orEmpty()
        val rawPath = path.orEmpty()
        val contentType = contentType.orEmpty()
        val directory = isDirectory
            || rawPath.endsWith("/")
            || hrefPath.endsWith("/")
            || contentType.equals("httpd/unix-directory", ignoreCase = true)
        val displayName = name?.takeIf { it.isNotBlank() }
            ?: rawPath.trim('/').substringAfterLast('/')
        val length = contentLength?.takeIf { it >= 0 } ?: 0L
        return WebDavResource(
            path = rawPath,
            name = displayName,
            isDirectory = directory,
            contentLength = length,
            modifiedMillis = modified?.time,
        )
    }
}
