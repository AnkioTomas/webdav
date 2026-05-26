package net.ankio.webdav.lib.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import net.ankio.theme.PreviewAllScreen
import net.ankio.theme.PreviewAllThemes
import net.ankio.theme.ThemePreviewConfig
import net.ankio.theme.ThemePreviewParameterProvider
import net.ankio.webdav.lib.ui.WebDavSettingsScreen

@PreviewAllScreen
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
            onSave = {},
            onTestStateChange = {},
        )
    }
}

@PreviewAllScreen
@Composable
private fun WebDavSettingsScreenSuccessPreview(
    @PreviewParameter(ThemePreviewParameterProvider::class) config: ThemePreviewConfig,
) {
    PreviewAllThemes(config) {
        WebDavSettingsScreen(
            state = WebDavPreviewSamples.settingsStateSuccess,
            onServerChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onSave = {},
            onTestStateChange = {},
        )
    }
}

@PreviewAllScreen
@Composable
private fun WebDavSettingsScreenFailurePreview(
    @PreviewParameter(ThemePreviewParameterProvider::class) config: ThemePreviewConfig,
) {
    PreviewAllThemes(config) {
        WebDavSettingsScreen(
            state = WebDavPreviewSamples.settingsStateFailure,
            onServerChange = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onSave = {},
            onTestStateChange = {},
        )
    }
}
