package net.ankio.webdav.demo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import net.ankio.theme.AnkioTheme
import net.ankio.theme.PreviewAll
import net.ankio.theme.PreviewAllThemes
import net.ankio.theme.ThemePreviewConfig
import net.ankio.theme.ThemePreviewParameterProvider
import net.ankio.theme.compat.ThemeCard
import net.ankio.theme.compat.ThemeIcon
import net.ankio.theme.compat.ThemeIconButton
import net.ankio.theme.compat.ThemeLinearProgressIndicator
import net.ankio.theme.compat.ThemeText
import net.ankio.webdav.demo.FilesUiState
import net.ankio.webdav.demo.R
import net.ankio.webdav.demo.preview.DemoPreviewSamples
import net.ankio.webdav.demo.ui.components.DemoTabScaffold
import net.ankio.webdav.lib.WebDavResource
import java.util.Locale

@Composable
fun DemoFilesScreen(
    state: FilesUiState,
    onRefresh: () -> Unit,
    onUploadTest: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToRoot: () -> Unit,
    onNavigateToSegment: (Int) -> Unit,
    onEnterDirectory: (WebDavResource) -> Unit,
    onDownload: (WebDavResource) -> Unit,
    onDelete: (WebDavResource) -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoTabScaffold(
        title = stringResource(R.string.files_title),
        modifier = modifier,
        scrollContent = false,
        actions = {
            val iconTint = AnkioTheme.colorScheme.onSurface
            if (state.relativePath.isNotBlank()) {
                ThemeIconButton(onClick = onNavigateUp) {
                    ThemeIcon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.files_go_up),
                        tint = iconTint,
                    )
                }
            }
            ThemeIconButton(onClick = onRefresh) {
                ThemeIcon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.files_refresh),
                    tint = iconTint,
                )
            }
            ThemeIconButton(onClick = onUploadTest) {
                ThemeIcon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = stringResource(R.string.files_upload_test),
                    tint = iconTint,
                )
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DemoPathBar(
                segments = state.pathSegments,
                onRootClick = onNavigateToRoot,
                onSegmentClick = onNavigateToSegment,
            )
            if (state.loading) {
                ThemeLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            state.statusMessage?.let { message ->
                ThemeText(
                    text = message,
                    style = AnkioTheme.textStyles.footnote1,
                    color = AnkioTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
            if (state.resources.isEmpty() && !state.loading) {
                ThemeText(
                    text = stringResource(R.string.files_empty),
                    style = AnkioTheme.textStyles.body2,
                    color = AnkioTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.resources, key = { it.path }) { resource ->
                        DemoFileItem(
                            resource = resource,
                            onOpenDirectory = { onEnterDirectory(resource) },
                            onDownload = { onDownload(resource) },
                            onDelete = { onDelete(resource) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoFileItem(
    resource: WebDavResource,
    onOpenDirectory: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
) {
    val summary = if (resource.isDirectory) {
        stringResource(R.string.files_item_dir)
    } else {
        stringResource(
            R.string.files_item_file,
            formatSize(resource.contentLength),
        )
    }
    val typeIcon = if (resource.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile
    val typeTint = if (resource.isDirectory) {
        AnkioTheme.colorScheme.primary
    } else {
        AnkioTheme.colorScheme.onSurfaceVariant
    }

    ThemeCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        onClick = if (resource.isDirectory) onOpenDirectory else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ThemeIcon(
                    imageVector = typeIcon,
                    contentDescription = null,
                    tint = typeTint,
                )
                Column {
                    ThemeText(
                        text = resource.name,
                        style = AnkioTheme.textStyles.title4,
                        color = AnkioTheme.colorScheme.onSurface,
                    )
                    ThemeText(
                        text = summary,
                        style = AnkioTheme.textStyles.footnote1,
                        color = AnkioTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!resource.isDirectory) {
                    ThemeIconButton(onClick = onDownload) {
                        ThemeIcon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(R.string.files_download),
                            tint = AnkioTheme.colorScheme.primary,
                        )
                    }
                }
                ThemeIconButton(onClick = onDelete) {
                    ThemeIcon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.files_delete),
                        tint = AnkioTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
    val mb = kb / 1024.0
    return String.format(Locale.US, "%.1f MB", mb)
}

@PreviewAll
@Composable
private fun DemoFilesScreenPreview(
    @PreviewParameter(ThemePreviewParameterProvider::class) config: ThemePreviewConfig,
) {
    PreviewAllThemes(config) {
        DemoFilesScreen(
            state = DemoPreviewSamples.filesUiState,
            onRefresh = {},
            onUploadTest = {},
            onNavigateUp = {},
            onNavigateToRoot = {},
            onNavigateToSegment = {},
            onEnterDirectory = {},
            onDownload = {},
            onDelete = {},
        )
    }
}
