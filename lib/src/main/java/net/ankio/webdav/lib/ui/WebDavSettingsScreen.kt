package net.ankio.webdav.lib.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.ankio.theme.AnkioTheme
import net.ankio.theme.compat.ThemeCard
import net.ankio.theme.compat.ThemeLinearProgressIndicator
import net.ankio.theme.compat.ThemePrimaryButton
import net.ankio.theme.compat.ThemeSecondaryButton
import net.ankio.theme.compat.ThemeText
import net.ankio.theme.settings.SettingCardPosition
import net.ankio.theme.settings.SettingInputMode
import net.ankio.theme.settings.ThemeSectionHeader
import net.ankio.theme.settings.ThemeSettingTextField
import net.ankio.theme.settings.toShape
import net.ankio.theme.settings.toVerticalPadding
import net.ankio.webdav.lib.R
import net.ankio.webdav.lib.WebDavConfig
import net.ankio.webdav.lib.WebDavTest
import net.ankio.webdav.lib.WebDavTestResult

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
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        ThemeSectionHeader(stringResource(R.string.webdav_section))

        ThemeSettingTextField(
            value = state.serverUrl,
            onValueChange = onServerChange,
            title = stringResource(R.string.webdav_server),
            placeholder = stringResource(R.string.webdav_server_hint),
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
            inputMode = SettingInputMode.Password,
            startAction = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = AnkioTheme.colorScheme.primary,
                )
            },
            position = SettingCardPosition.Middle,
        )

        val lastPos = SettingCardPosition.Last
        val (lastTop, lastBottom) = lastPos.toVerticalPadding()
        ThemeCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = lastTop, bottom = lastBottom),
            shape = lastPos.toShape(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        text = stringResource(R.string.webdav_save),
                    )
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
                        text = stringResource(R.string.webdav_test),
                    )
                }

                WebDavTestResultContent(
                    testState = state.testState,
                    testingMessage = testingMessage,
                    successMessage = successMessage,
                    savedMessage = savedMessage,
                )
            }
        }
    }
}

@Composable
internal fun WebDavTestResultContent(
    testState: WebDavTestUiState,
    testingMessage: String,
    successMessage: String,
    savedMessage: String,
) {
    val idleMessage = stringResource(R.string.webdav_test_result_idle)
    val title = stringResource(R.string.webdav_test_result_title)

    Column(modifier = Modifier.fillMaxWidth()) {
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
