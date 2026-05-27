package net.ankio.webdav.demo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.ankio.webdav.demo.R
import net.ankio.webdav.demo.ui.components.DemoTabScaffold
import net.ankio.webdav.lib.WebDavConfig
import net.ankio.webdav.lib.ui.WebDavSettingsScreen

@Composable
fun DemoSettingsTab(
    modifier: Modifier = Modifier,
    onConfigSaved: (WebDavConfig) -> Unit = {},
) {
    DemoTabScaffold(
        title = stringResource(R.string.tab_settings),
        modifier = modifier,
        scrollContent = true,
    ) {
        WebDavSettingsScreen(onSaved = onConfigSaved)
    }
}
