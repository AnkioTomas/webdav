package net.ankio.webdav.lib

import android.app.Application
import android.content.Context
import net.ankio.theme.ThemeSettings
import net.ankio.theme.toast.ThemeToast

/**
 * WebDAV 库入口。
 */
object WebDav {

    fun init(application: Application) {
        ThemeSettings.init(application)
        ThemeToast.init(application)
    }

    fun client(config: WebDavConfig): WebDavClient = WebDavClient.create(config)

    fun client(context: Context): WebDavClient = client(WebDavConfigStore.load(context))
}
