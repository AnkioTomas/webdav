package net.ankio.webdav.lib

sealed interface WebDavTestResult {
    data object Success : WebDavTestResult

    data class Failure(val message: String) : WebDavTestResult
}
