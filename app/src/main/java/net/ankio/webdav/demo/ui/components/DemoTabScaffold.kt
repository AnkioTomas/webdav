package net.ankio.webdav.demo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import net.ankio.theme.compat.ThemeTopAppBar
import net.ankio.theme.compat.ThemeTopAppBarTitleAlignment
import net.ankio.theme.compat.rememberThemeTopAppBarScroll

@Composable
fun DemoTabScaffold(
    title: String,
    modifier: Modifier = Modifier,
    scrollContent: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val scroll = rememberThemeTopAppBarScroll(collapseOnScroll = true)
    val nestedScrollModifier = scroll?.let { Modifier.nestedScroll(it.nestedScrollConnection) } ?: Modifier

    Column(modifier = modifier.fillMaxSize()) {
        ThemeTopAppBar(
            title = title,
            largeTitle = title,
            titleAlignment = ThemeTopAppBarTitleAlignment.Start,
            scroll = scroll,
            modifier = Modifier.fillMaxWidth(),
            actions = actions,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(nestedScrollModifier),
        ) {
            if (scrollContent) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    content()
                }
            } else {
                content()
            }
        }
    }
}
