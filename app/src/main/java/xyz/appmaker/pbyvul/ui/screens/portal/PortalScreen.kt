package xyz.appmaker.pbyvul.ui.screens.portal

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun BrowserScreen(
    address: String
) {
    val context = LocalContext.current

    LaunchedEffect(address) {

        val intent = Intent(
            context,
            BrowserActivity::class.java
        )

        intent.putExtra(
            BrowserActivity.EXTRA_URL,
            address
        )

        context.startActivity(intent)

        if (context is Activity) {
            context.finish()
        }
    }
}