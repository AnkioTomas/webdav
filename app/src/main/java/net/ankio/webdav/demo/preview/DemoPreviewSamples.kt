package net.ankio.webdav.demo.preview

import net.ankio.webdav.demo.FilesUiState
import net.ankio.webdav.lib.WebDavResource

object DemoPreviewSamples {
    val filesUiState = FilesUiState(
        relativePath = "demo",
        resources = listOf(
            WebDavResource(
                path = "https://dav.example.com/dav/demo/",
                name = "notes",
                isDirectory = true,
                contentLength = 0,
                modifiedMillis = null,
            ),
            WebDavResource(
                path = "https://dav.example.com/dav/demo/readme.txt",
                name = "readme.txt",
                isDirectory = false,
                contentLength = 2048,
                modifiedMillis = null,
            ),
        ),
        loading = false,
        statusMessage = "已加载 2 项",
    )
}
