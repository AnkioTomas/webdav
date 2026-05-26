package net.ankio.webdav.lib.ui.preview

import net.ankio.webdav.lib.ui.WebDavSettingsState

object WebDavPreviewSamples {
    val settingsState = WebDavSettingsState(
        serverUrl = "https://dav.example.com/dav/",
        username = "demo",
        password = "secret",
        testing = false,
    )

    val settingsStateTesting = settingsState.copy(testing = true)
}
