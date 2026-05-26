package net.ankio.webdav.lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WebDAV 连接测试。
 */
object WebDavTest {

    suspend fun run(config: WebDavConfig): WebDavTestResult = withContext(Dispatchers.IO) {
        if (!config.isValid()) {
            return@withContext WebDavTestResult.Failure("invalid config")
        }
        runCatching {
            val client = WebDavClient.create(config)
            client.list()
        }.fold(
            onSuccess = { WebDavTestResult.Success },
            onFailure = { WebDavTestResult.Failure(it.message ?: it.javaClass.simpleName) },
        )
    }
}
