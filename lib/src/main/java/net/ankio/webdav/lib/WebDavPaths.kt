package net.ankio.webdav.lib

/**
 * WebDAV 路径与 URL 规范化，避免各处重复 trim/join 逻辑。
 */
object WebDavPaths {

    fun normalizeBaseUrl(url: String): String = url.trim().trimEnd('/')

    fun normalizeRelative(path: String): String {
        val trimmed = path.trim().trim('/')
        return if (trimmed.isEmpty()) "" else "/$trimmed"
    }

    fun join(baseUrl: String, relativePath: String = ""): String {
        val base = normalizeBaseUrl(baseUrl)
        val relative = normalizeRelative(relativePath)
        return base + relative
    }
}
