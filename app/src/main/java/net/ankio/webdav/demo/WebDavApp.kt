package net.ankio.webdav.demo

import android.app.Application
import net.ankio.webdav.lib.WebDav

class WebDavApp : Application() {
    override fun onCreate() {
        super.onCreate()
        WebDav.init(this)
    }
}
