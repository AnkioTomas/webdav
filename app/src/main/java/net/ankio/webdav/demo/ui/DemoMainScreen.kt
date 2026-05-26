package net.ankio.webdav.demo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.ankio.theme.compat.ThemeNavigationBar
import net.ankio.theme.compat.ThemeNavigationBarItem
import net.ankio.webdav.demo.DemoTab
import net.ankio.webdav.demo.DemoViewModel

@Composable
fun DemoMainScreen(
    viewModel: DemoViewModel,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(DemoTab.Settings) }
    val filesState by viewModel.filesState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                DemoTab.Settings -> DemoSettingsTab(
                    onConfigSaved = { viewModel.refreshFiles() },
                )

                DemoTab.Files -> DemoFilesScreen(
                    state = filesState,
                    onRefresh = viewModel::refreshFiles,
                    onUploadTest = viewModel::uploadTestFile,
                    onNavigateUp = viewModel::navigateUp,
                    onEnterDirectory = viewModel::enterDirectory,
                    onDelete = viewModel::deleteResource,
                )
            }
        }

        ThemeNavigationBar {
            DemoTab.entries.forEach { tab ->
                ThemeNavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    icon = tab.icon,
                    label = stringResource(tab.titleRes),
                )
            }
        }
    }
}
