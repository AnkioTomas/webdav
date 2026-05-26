package net.ankio.webdav.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.ankio.webdav.lib.OnConfigSaved
import net.ankio.webdav.lib.WebDav
import net.ankio.webdav.lib.WebDavConfig
import net.ankio.webdav.lib.WebDavConfigStore
import net.ankio.webdav.lib.WebDavResource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FilesUiState(
    val relativePath: String = "",
    val resources: List<WebDavResource> = emptyList(),
    val loading: Boolean = false,
    val statusMessage: String? = null,
) {
    val pathSegments: List<String>
        get() = relativePath.split('/').filter { it.isNotEmpty() }
}

class DemoViewModel(application: Application) : AndroidViewModel(application) {

    private val context get() = getApplication<Application>()

    private val _filesState = MutableStateFlow(FilesUiState())
    val filesState = _filesState.asStateFlow()

    private var cachedConfig: WebDavConfig? = null

    private val configListener: OnConfigSaved = { _, config ->
        cachedConfig = config
    }

    init {
        cachedConfig = WebDavConfigStore.load(context)
        WebDavConfigStore.addOnSaveListener(configListener)
        refreshFiles()
    }

    override fun onCleared() {
        WebDavConfigStore.removeOnSaveListener(configListener)
        super.onCleared()
    }

    fun refreshFiles() {
        val config = currentConfig()
        if (!config.isValid()) {
            _filesState.update {
                it.copy(
                    loading = false,
                    resources = emptyList(),
                    statusMessage = context.getString(R.string.files_config_required),
                )
            }
            return
        }
        viewModelScope.launch {
            _filesState.update { it.copy(loading = true, statusMessage = null) }
            runCatching {
                WebDav.client(config).list(_filesState.value.relativePath)
            }.fold(
                onSuccess = { items ->
                    _filesState.update {
                        it.copy(
                            loading = false,
                            resources = items,
                            statusMessage = context.getString(R.string.files_list_ok, items.size),
                        )
                    }
                },
                onFailure = { error ->
                    _filesState.update {
                        it.copy(
                            loading = false,
                            resources = emptyList(),
                            statusMessage = context.getString(
                                R.string.files_list_failed,
                                error.message ?: error.javaClass.simpleName,
                            ),
                        )
                    }
                },
            )
        }
    }

    fun enterDirectory(resource: WebDavResource) {
        if (!resource.isDirectory) {
            _filesState.update {
                it.copy(
                    statusMessage = context.getString(
                        R.string.files_open_dir_failed,
                        resource.name,
                    ),
                )
            }
            return
        }
        _filesState.update {
            it.copy(relativePath = joinPath(it.relativePath, resource.name))
        }
        refreshFiles()
    }

    fun navigateUp() {
        val segments = _filesState.value.pathSegments
        if (segments.isEmpty()) return
        val parent = segments.dropLast(1).joinToString("/")
        _filesState.update { it.copy(relativePath = parent) }
        refreshFiles()
    }

    fun navigateToRoot() {
        if (_filesState.value.relativePath.isEmpty()) return
        _filesState.update { it.copy(relativePath = "") }
        refreshFiles()
    }

    fun navigateToSegment(segmentIndex: Int) {
        val segments = _filesState.value.pathSegments
        if (segmentIndex !in segments.indices) return
        val target = segments.take(segmentIndex + 1).joinToString("/")
        if (target == _filesState.value.relativePath) return
        _filesState.update { it.copy(relativePath = target) }
        refreshFiles()
    }

    fun uploadTestFile() {
        val config = currentConfig()
        if (!config.isValid()) return
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val relative = joinPath(_filesState.value.relativePath, "demo-$stamp.txt")
        val payload = "WebDAV demo @ $stamp".toByteArray()
        viewModelScope.launch {
            _filesState.update { it.copy(loading = true) }
            runCatching {
                WebDav.client(config).writeBytes(relative, payload, "text/plain")
            }.fold(
                onSuccess = {
                    _filesState.update {
                        it.copy(
                            statusMessage = context.getString(R.string.files_upload_ok, relative),
                        )
                    }
                    refreshFiles()
                },
                onFailure = { error ->
                    _filesState.update {
                        it.copy(
                            loading = false,
                            statusMessage = context.getString(
                                R.string.files_upload_failed,
                                error.message ?: error.javaClass.simpleName,
                            ),
                        )
                    }
                },
            )
        }
    }

    fun downloadFile(resource: WebDavResource) {
        if (resource.isDirectory) return
        val config = currentConfig()
        if (!config.isValid()) return
        val relative = joinPath(_filesState.value.relativePath, resource.name)
        viewModelScope.launch {
            _filesState.update { it.copy(loading = true, statusMessage = null) }
            runCatching {
                val bytes = WebDav.client(config).readBytes(relative)
                DemoDownload.saveToDownloads(context, resource.name, bytes)
            }.fold(
                onSuccess = { savedName ->
                    _filesState.update {
                        it.copy(
                            loading = false,
                            statusMessage = context.getString(R.string.files_download_ok, savedName),
                        )
                    }
                },
                onFailure = { error ->
                    _filesState.update {
                        it.copy(
                            loading = false,
                            statusMessage = context.getString(
                                R.string.files_download_failed,
                                error.message ?: error.javaClass.simpleName,
                            ),
                        )
                    }
                },
            )
        }
    }

    fun deleteResource(resource: WebDavResource) {
        val config = currentConfig()
        if (!config.isValid()) return
        val relative = joinPath(_filesState.value.relativePath, resource.name)
        viewModelScope.launch {
            _filesState.update { it.copy(loading = true) }
            runCatching {
                WebDav.client(config).delete(relative)
            }.fold(
                onSuccess = {
                    _filesState.update {
                        it.copy(
                            statusMessage = context.getString(R.string.files_delete_ok, resource.name),
                        )
                    }
                    refreshFiles()
                },
                onFailure = { error ->
                    _filesState.update {
                        it.copy(
                            loading = false,
                            statusMessage = context.getString(
                                R.string.files_delete_failed,
                                error.message ?: error.javaClass.simpleName,
                            ),
                        )
                    }
                },
            )
        }
    }

    private fun currentConfig(): WebDavConfig =
        cachedConfig ?: WebDavConfigStore.load(context).also { cachedConfig = it }

    private fun joinPath(base: String, name: String): String {
        val normalizedBase = base.trim('/')
        val normalizedName = name.trim('/')
        return when {
            normalizedBase.isEmpty() -> normalizedName
            normalizedName.isEmpty() -> normalizedBase
            else -> "$normalizedBase/$normalizedName"
        }
    }
}
