package net.ankio.webdav.lib

import java.util.Locale

/** 规范化 WebDAV 目录列表结果。 */
object WebDavListing {

    fun normalize(entries: List<WebDavResource>, listRelativePath: String = ""): List<WebDavResource> {
        val currentPath = listRelativePath.trim('/').lowercase(Locale.ROOT)
        return entries
            .filter { entry -> entry.isListableEntry(currentPath) }
            .distinctBy { it.path.ifBlank { it.name }.lowercase(Locale.ROOT) }
            .sortedWith(
                compareBy<WebDavResource>(
                    { !it.isDirectory },
                    { it.name.lowercase(Locale.ROOT) },
                ),
            )
    }

    private fun WebDavResource.isListableEntry(currentPath: String): Boolean {
        if (name.isBlank() || name == "." || name == "..") return false
        if (currentPath.isEmpty()) return true
        if (!isDirectory) return true
        val entryPath = path.trim('/').lowercase(Locale.ROOT)
        val currentName = currentPath.substringAfterLast('/')
        if (!name.equals(currentName, ignoreCase = true)) return true
        return entryPath != currentPath && !entryPath.endsWith("/$currentPath")
    }
}
