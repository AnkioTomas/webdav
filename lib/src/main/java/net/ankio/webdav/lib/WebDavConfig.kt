package net.ankio.webdav.lib

/**
 * WebDAV 连接配置。
 */
data class WebDavConfig(
    val serverUrl: String,
    val username: String,
    val password: String,
) {
    val baseUrl: String
        get() = WebDavPaths.normalizeBaseUrl(serverUrl)

    fun isValid(): Boolean {
        val hasScheme = baseUrl.startsWith("http://", ignoreCase = true) ||
            baseUrl.startsWith("https://", ignoreCase = true)
        return hasScheme && username.isNotBlank()
    }
}
