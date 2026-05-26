package net.ankio.webdav.lib.ui

/** 连接测试 / 保存反馈在 UI 上的展示状态。 */
sealed interface WebDavTestUiState {
    data object Idle : WebDavTestUiState
    data object Running : WebDavTestUiState
    data object Success : WebDavTestUiState
    data class Failure(val message: String) : WebDavTestUiState
    data object Saved : WebDavTestUiState
}
