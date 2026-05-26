package net.ankio.webdav.lib

/**
 * WebDAV 目录项。
 */
data class WebDavResource(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val contentLength: Long,
    val modifiedMillis: Long?,
)
