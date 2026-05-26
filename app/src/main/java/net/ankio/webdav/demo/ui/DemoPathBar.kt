package net.ankio.webdav.demo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.ankio.theme.AnkioTheme
import net.ankio.theme.compat.ThemeText
import net.ankio.webdav.demo.R

@Composable
fun DemoPathBar(
    segments: List<String>,
    onRootClick: () -> Unit,
    onSegmentClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        PathChip(
            label = stringResource(R.string.files_path_root_label),
            onClick = onRootClick,
        )
        segments.forEachIndexed { index, segment ->
            PathSeparator()
            PathChip(
                label = segment,
                onClick = { onSegmentClick(index) },
                emphasized = index == segments.lastIndex,
            )
        }
    }
}

@Composable
private fun PathSeparator() {
    ThemeText(
        text = " / ",
        style = AnkioTheme.textStyles.body2,
        color = AnkioTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 2.dp),
    )
}

@Composable
private fun PathChip(
    label: String,
    onClick: () -> Unit,
    emphasized: Boolean = false,
) {
    ThemeText(
        text = label,
        style = if (emphasized) AnkioTheme.textStyles.title4 else AnkioTheme.textStyles.body2,
        color = if (emphasized) {
            AnkioTheme.colorScheme.primary
        } else {
            AnkioTheme.colorScheme.onSurface
        },
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 2.dp),
    )
}
