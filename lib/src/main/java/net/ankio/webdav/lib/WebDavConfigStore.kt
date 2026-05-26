package net.ankio.webdav.lib

import android.content.Context
import androidx.core.content.edit
import java.util.concurrent.CopyOnWriteArrayList

/** 配置保存完成时触发：`(context, config) -> Unit` */
typealias OnConfigSaved = (context: Context, config: WebDavConfig) -> Unit

/**
 * WebDAV 配置持久化。
 */
object WebDavConfigStore {

    private const val PREFS_NAME = "net.ankio.webdav.config"
    private const val KEY_SERVER = "server"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"

    private const val DEFAULT_SERVER = "https://dav.jianguoyun.com/dav/"

    private val listeners = CopyOnWriteArrayList<OnConfigSaved>()

    fun addOnSaveListener(listener: OnConfigSaved) {
        listeners.add(listener)
    }

    fun removeOnSaveListener(listener: OnConfigSaved) {
        listeners.remove(listener)
    }

    fun clearOnSaveListeners() {
        listeners.clear()
    }

    fun load(context: Context): WebDavConfig {
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return WebDavConfig(
            serverUrl = prefs.getString(KEY_SERVER, DEFAULT_SERVER).orEmpty(),
            username = prefs.getString(KEY_USERNAME, "").orEmpty(),
            password = prefs.getString(KEY_PASSWORD, "").orEmpty(),
        )
    }

    fun save(
        context: Context,
        config: WebDavConfig,
        onSaved: OnConfigSaved? = null,
    ) {
        val appContext = context.applicationContext
        appContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_SERVER, config.serverUrl)
                putString(KEY_USERNAME, config.username)
                putString(KEY_PASSWORD, config.password)
            }
        dispatchSaved(appContext, config, onSaved)
    }

    private fun dispatchSaved(
        context: Context,
        config: WebDavConfig,
        onSaved: OnConfigSaved?,
    ) {
        onSaved?.invoke(context, config)
        listeners.forEach { it(context, config) }
    }
}
