package net.ankio.webdav.lib.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.ankio.theme.AnkioTheme
import net.ankio.theme.PreviewAll
import net.ankio.theme.PreviewAllThemes
import net.ankio.theme.ThemePreviewConfig
import net.ankio.theme.ThemePreviewParameterProvider
import net.ankio.theme.compat.ThemeCard
import net.ankio.theme.compat.ThemeLinearProgressIndicator
import net.ankio.theme.compat.ThemePrimaryButton
import net.ankio.theme.compat.ThemeSecondaryButton
import net.ankio.theme.compat.ThemeText
import net.ankio.theme.compat.ThemeTextField
import net.ankio.theme.settings.ThemeSectionHeader
import net.ankio.webdav.lib.R
import net.ankio.webdav.lib.WebDavConfig
import net.ankio.webdav.lib.WebDavTest
import net.ankio.webdav.lib.WebDavTestResult
import net.ankio.webdav.lib.ui.preview.WebDavPreviewSamples

/**
 * WebDAV 配置 Compose 页面。
 */
@Composable
fun WebDavSettingsScreen(
    state: WebDavSettingsState,
    onServerChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    onTestStateChange: (WebDavTestUiState) -> Unit,
    modifier: Modifier = Modifier,
    /** 保存/测试前额外校验，返回错误文案时中止操作。 */
    configValidator: (WebDavConfig) -> String? = { null },
) {
    val scope = rememberCoroutineScope()
    val invalidMessage = stringResource(R.string.webdav_config_invalid)
    val testingMessage = stringResource(R.string.webdav_test_running)
    val successMessage = stringResource(R.string.webdav_test_success)
    val failedTemplate = stringResource(R.string.webdav_test_failed)
    val savedMessage = stringResource(R.string.webdav_saved)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ThemeSectionHeader(stringResource(R.string.webdav_section))

        ThemeCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WebDavConfigField(
                    icon = Icons.Filled.Link,
                    label = stringResource(R.string.webdav_server),
                    value = state.serverUrl,
                    onValueChange = onServerChange,
                    placeholder = stringResource(R.string.webdav_server_hint),
                )
                WebDavConfigField(
                    icon = Icons.Filled.Person,
                    label = stringResource(R.string.webdav_username),
                    value = state.username,
                    onValueChange = onUsernameChange,
                )
                WebDavConfigField(
                    icon = Icons.Filled.Lock,
                    label = stringResource(R.string.webdav_password),
                    value = state.password,
                    onValueChange = onPasswordChange,
                    isPassword = true,
                )
            }
        }

        ThemeCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ThemeSecondaryButton(
                    onClick = {
                        val config = state.toConfig()
                        if (!config.isValid()) {
                            onTestStateChange(WebDavTestUiState.Failure(invalidMessage))
                            return@ThemeSecondaryButton
                        }
                        configValidator(config)?.let { message ->
                            onTestStateChange(WebDavTestUiState.Failure(message))
                            return@ThemeSecondaryButton
                        }
                        onSave()
                        onTestStateChange(WebDavTestUiState.Saved)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isTesting,
                ) {
                    ThemeText(
                        text = stringResource(R.string.webdav_save),
                        style = AnkioTheme.textStyles.button,
                        color = AnkioTheme.colorScheme.onSecondaryContainer,
                    )
                }
                ThemePrimaryButton(
                    onClick = {
                        if (state.isTesting) return@ThemePrimaryButton
                        val config = state.toConfig()
                        if (!config.isValid()) {
                            onTestStateChange(WebDavTestUiState.Failure(invalidMessage))
                            return@ThemePrimaryButton
                        }
                        configValidator(config)?.let { message ->
                            onTestStateChange(WebDavTestUiState.Failure(message))
                            return@ThemePrimaryButton
                        }
                        scope.launch {
                            onTestStateChange(WebDavTestUiState.Running)
                            when (val result = WebDavTest.run(config)) {
                                WebDavTestResult.Success -> {
                                    onSave()
                                    onTestStateChange(WebDavTestUiState.Success)
                                }

                                is WebDavTestResult.Failure -> {
                                    val detail = result.message.ifBlank { "unknown" }
                                    onTestStateChange(
                                        WebDavTestUiState.Failure(failedTemplate.format(detail)),
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isTesting,
                ) {
                    ThemeText(
                        text = stringResource(R.string.webdav_test),
                        style = AnkioTheme.textStyles.button,
                        color = AnkioTheme.colorScheme.onPrimary,
                    )
                }
            }
        }

        WebDavTestResultPanel(
            testState = state.testState,
            testingMessage = testingMessage,
            successMessage = successMessage,
            savedMessage = savedMessage,
        )
    }
}

@Composable
private fun WebDavConfigField(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
) {
    ThemeTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = label,
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AnkioTheme.colorScheme.primary,
            )
        },
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
    )
}

@Composable
private fun WebDavTestResultPanel(
    testState: WebDavTestUiState,
    testingMessage: String,
    successMessage: String,
    savedMessage: String,
) {
    val idleMessage = stringResource(R.string.webdav_test_result_idle)
    val title = stringResource(R.string.webdav_test_result_title)

    ThemeCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        containerColor = AnkioTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            ThemeText(
                text = title,
                style = AnkioTheme.textStyles.title4,
                color = AnkioTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            when (testState) {
                WebDavTestUiState.Idle -> {
                    ThemeText(
                        text = idleMessage,
                        style = AnkioTheme.textStyles.body2,
                        color = AnkioTheme.colorScheme.onSurfaceVariant,
                    )
                }

                WebDavTestUiState.Running -> {
                    ThemeLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    ThemeText(
                        text = testingMessage,
                        style = AnkioTheme.textStyles.body2,
                        color = AnkioTheme.colorScheme.primary,
                    )
                }

                WebDavTestUiState.Success -> {
                    ThemeText(
                        text = successMessage,
                        style = AnkioTheme.textStyles.body2,
                        color = AnkioTheme.colorScheme.primary,
                    )
                }

                is WebDavTestUiState.Failure -> {
                    ThemeText(
                        text = testState.message,
                        style = AnkioTheme.textStyles.body2,
                        color = AnkioTheme.colorScheme.error,
                    )
                }

                WebDavTestUiState.Saved -> {
                    ThemeText(
                        text = savedMessage,
                        style = AnkioTheme.textStyles.body2,
                        color = AnkioTheme.colorScheme.primary,
                    )
                }
            }
        }
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
            onSave = {},
            onTestStateChange = {},
        )
    }
}

@PreviewAll
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
