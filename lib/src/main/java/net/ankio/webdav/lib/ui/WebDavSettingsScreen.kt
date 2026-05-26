package net.ankio.webdav.lib.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.ankio.theme.PreviewAll
import net.ankio.theme.PreviewAllThemes
import net.ankio.theme.ThemePreviewConfig
import net.ankio.theme.ThemePreviewParameterProvider
import net.ankio.webdav.lib.ui.preview.WebDavPreviewSamples
import net.ankio.theme.AnkioTheme
import net.ankio.theme.settings.SettingCardPosition
import net.ankio.theme.settings.ThemeSectionHeader
import net.ankio.theme.settings.ThemeSettingClick
import net.ankio.theme.settings.ThemeSettingTextField
import net.ankio.theme.toast.ThemeToast
import net.ankio.webdav.lib.R
import net.ankio.webdav.lib.WebDavTest
import net.ankio.webdav.lib.WebDavTestResult

/**
 * WebDAV 配置 Compose 页面，基于 theme 库设置组件。
 */
@Composable
fun WebDavSettingsScreen(
    state: WebDavSettingsState,
    onServerChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTestingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val invalidMessage = stringResource(R.string.webdav_config_invalid)
    val testingMessage = stringResource(R.string.webdav_test_running)
    val successMessage = stringResource(R.string.webdav_test_success)
    val failedTemplate = stringResource(R.string.webdav_test_failed)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        ThemeSectionHeader(stringResource(R.string.webdav_section))

        ThemeSettingTextField(
            value = state.serverUrl,
            onValueChange = onServerChange,
            title = stringResource(R.string.webdav_server),
            label = stringResource(R.string.webdav_server_hint),
            startAction = {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = null,
                    tint = AnkioTheme.colorScheme.primary,
                )
            },
            position = SettingCardPosition.First,
        )

        ThemeSettingTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            title = stringResource(R.string.webdav_username),
            label = stringResource(R.string.webdav_username),
            startAction = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = AnkioTheme.colorScheme.primary,
                )
            },
            position = SettingCardPosition.Middle,
        )

        ThemeSettingTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            title = stringResource(R.string.webdav_password),
            label = stringResource(R.string.webdav_password),
            startAction = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = AnkioTheme.colorScheme.primary,
                )
            },
            position = SettingCardPosition.Middle,
        )

        ThemeSettingClick(
            title = stringResource(R.string.webdav_test),
            summary = if (state.testing) testingMessage else null,
            onClick = {
                if (state.testing) return@ThemeSettingClick
                val config = state.toConfig()
                if (!config.isValid()) {
                    ThemeToast.show(invalidMessage, ThemeToast.Style.Warning)
                    return@ThemeSettingClick
                }
                scope.launch {
                    onTestingChange(true)
                    ThemeToast.show(testingMessage, ThemeToast.Style.Info)
                    when (val result = WebDavTest.run(config)) {
                        WebDavTestResult.Success ->
                            ThemeToast.show(successMessage, ThemeToast.Style.Success)

                        is WebDavTestResult.Failure -> {
                            val detail = result.message.ifBlank { "unknown" }
                            ThemeToast.show(
                                failedTemplate.format(detail),
                                ThemeToast.Style.Error,
                            )
                        }
                    }
                    onTestingChange(false)
                }
            },
            startAction = {
                Icon(
                    imageVector = Icons.Filled.CloudSync,
                    contentDescription = null,
                    tint = AnkioTheme.colorScheme.primary,
                )
            },
            position = SettingCardPosition.Last,
        )
    }
}

@PreviewAll
@Composable
private fun WebDavSettingsScreenPreview(
    @PreviewParameter(ThemePreviewParameterProvider::class) config: ThemePreviewConfig,
) {
    PreviewAllThemes(config) {
        WebDavSettingsScreen(
            state = WebDavPreviewSamples.settingsState,
            onServerChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onTestingChange = {},
        )
    }
}

@PreviewAll
@Composable
private fun WebDavSettingsScreenTestingPreview(
    @PreviewParameter(ThemePreviewParameterProvider::class) config: ThemePreviewConfig,
) {
    PreviewAllThemes(config) {
        WebDavSettingsScreen(
            state = WebDavPreviewSamples.settingsStateTesting,
            onServerChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onTestingChange = {},
        )
    }
}
