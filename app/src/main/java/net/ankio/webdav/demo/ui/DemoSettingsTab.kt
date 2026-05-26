package net.ankio.webdav.demo.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import net.ankio.webdav.demo.R
import net.ankio.webdav.demo.ui.components.DemoTabScaffold
import net.ankio.webdav.lib.WebDavConfig
import net.ankio.webdav.lib.WebDavConfigStore
import net.ankio.webdav.lib.ui.WebDavSettingsScreen
import net.ankio.webdav.lib.ui.WebDavSettingsState
import net.ankio.webdav.lib.ui.WebDavTestUiState

@Composable
fun DemoSettingsTab(
    modifier: Modifier = Modifier,
    onConfigSaved: (WebDavConfig) -> Unit = {},
) {
    val context = LocalContext.current
    val saved = remember { WebDavConfigStore.load(context) }
    var serverUrl by rememberSaveable { mutableStateOf(saved.serverUrl) }
    var username by rememberSaveable { mutableStateOf(saved.username) }
    var password by rememberSaveable { mutableStateOf(saved.password) }
    var testState by remember { mutableStateOf<WebDavTestUiState>(WebDavTestUiState.Idle) }

    DemoTabScaffold(
        title = stringResource(R.string.tab_settings),
        modifier = modifier,
        scrollContent = true,
    ) {
        WebDavSettingsScreen(
            state = WebDavSettingsState(
                serverUrl = serverUrl,
                username = username,
                password = password,
                testState = testState,
            ),
            onServerChange = { serverUrl = it },
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onSave = {
                val config = WebDavConfig(serverUrl, username, password)
                WebDavConfigStore.save(context, config) { _, savedConfig ->
                    onConfigSaved(savedConfig)
                }
            },
            onTestStateChange = { testState = it },
        )
    }
}
