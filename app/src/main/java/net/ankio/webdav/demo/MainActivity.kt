package net.ankio.webdav.demo

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import net.ankio.theme.BaseComposeActivity
import net.ankio.webdav.demo.ui.DemoMainScreen

class MainActivity : BaseComposeActivity() {

    private val viewModel: DemoViewModel by viewModels()

    @Composable
    override fun Content() {
        DemoMainScreen(viewModel = viewModel)
    }
}
