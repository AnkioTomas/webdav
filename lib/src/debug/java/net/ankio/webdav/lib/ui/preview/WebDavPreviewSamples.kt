package net.ankio.webdav.lib.ui.preview

import net.ankio.webdav.lib.ui.WebDavSettingsState
import net.ankio.webdav.lib.ui.WebDavTestUiState

object WebDavPreviewSamples {
    val settingsState = WebDavSettingsState(
        serverUrl = "https://dav.example.com/dav/",
        username = "demo",
        password = "secret",
    )

    val settingsStateSuccess = settingsState.copy(
        testState = WebDavTestUiState.Success,
    )

    val settingsStateFailure = settingsState.copy(
        testState = WebDavTestUiState.Failure("连接失败：401 Unauthorized"),
    )
}
