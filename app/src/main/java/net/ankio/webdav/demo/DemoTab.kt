package net.ankio.webdav.demo

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class DemoTab(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
) {
    Settings(R.string.tab_settings, Icons.Default.Settings),
    Files(R.string.tab_files, Icons.Default.Folder),
}
