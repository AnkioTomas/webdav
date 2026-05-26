package net.ankio.webdav.lib.ui

import net.ankio.webdav.lib.WebDavConfig

/**
 * WebDAV 设置页 UI 状态。
 */
data class WebDavSettingsState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val testing: Boolean = false,
) {
    fun toConfig() = WebDavConfig(
        serverUrl = serverUrl,
        username = username,
        password = password,
    )
}
