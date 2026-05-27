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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import net.ankio.webdav.lib.WebDavConfigStore
import net.ankio.webdav.lib.WebDavTest
import net.ankio.webdav.lib.WebDavTestResult

/**
 * WebDAV 配置 Compose 页面。
 *
 * 「保存」与连接测试成功时会将当前表单写入 [WebDavConfigStore]，之后调用 [onSaved]（若不需要可省略）。
 */
@Composable
fun WebDavSettingsScreen(
    state: WebDavSettingsState? = null,
    modifier: Modifier = Modifier,
    /** 已在内部写入 [WebDavConfigStore] 后回调，便于宿主刷新 UI 等。 */
    onSaved: (WebDavConfig) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val initialState = remember(state, context) {
        state ?: WebDavConfigStore.load(context).let { config ->
            WebDavSettingsState(
                serverUrl = config.serverUrl,
                username = config.username,
                password = config.password,
            )
        }
    }
    var serverUrl by rememberSaveable(initialState.serverUrl) { mutableStateOf(initialState.serverUrl) }
    var username by rememberSaveable(initialState.username) { mutableStateOf(initialState.username) }
    var password by rememberSaveable(initialState.password) { mutableStateOf(initialState.password) }
    // WebDavTestUiState 非 Bundle 可序列化类型，不能使用 rememberSaveable；进程杀死后重置为初始值即可。
    var testState by remember(initialState) { mutableStateOf(initialState.testState) }
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
            value = serverUrl,
            onValueChange = { serverUrl = it },
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
            value = username,
            onValueChange = { username = it },
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
            value = password,
            onValueChange = { password = it },
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
                            val config = WebDavConfig(serverUrl, username, password)
                            if (!config.isValid()) {
                                testState = WebDavTestUiState.Failure(invalidMessage)
                                return@ThemeSecondaryButton
                            }
                            WebDavConfigStore.save(context, config) { _, saved ->
                                onSaved(saved)
                            }
                            testState = WebDavTestUiState.Saved
                        },
                        modifier = Modifier.weight(1f),
                        enabled = testState !is WebDavTestUiState.Running,
                        text = stringResource(R.string.webdav_save),
                    )
                    ThemePrimaryButton(
                        onClick = {
                            if (testState is WebDavTestUiState.Running) return@ThemePrimaryButton
                            val config = WebDavConfig(serverUrl, username, password)
                            if (!config.isValid()) {
                                testState = WebDavTestUiState.Failure(invalidMessage)
                                return@ThemePrimaryButton
                            }
                            scope.launch {
                                testState = WebDavTestUiState.Running
                                when (val result = WebDavTest.run(config)) {
                                    WebDavTestResult.Success -> {
                                        WebDavConfigStore.save(context, config) { _, saved ->
                                            onSaved(saved)
                                        }
                                        testState = WebDavTestUiState.Success
                                    }

                                    is WebDavTestResult.Failure -> {
                                        val detail = result.message.ifBlank { "unknown" }
                                        testState = WebDavTestUiState.Failure(failedTemplate.format(detail))
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = testState !is WebDavTestUiState.Running,
                        text = stringResource(R.string.webdav_test),
                    )
                }

                WebDavTestResultContent(
                    testState = testState,
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
