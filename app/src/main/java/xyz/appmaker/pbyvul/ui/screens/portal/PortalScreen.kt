package xyz.appmaker.pbyvul.ui.screens.portal

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled", "SourceLockedOrientationActivity")
@Composable
fun PortalScreen(
    address: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        hideSystemBars(activity)
        onDispose {
            CookieManager.getInstance().flush()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            showSystemBars(activity)
        }
    }

    var isInitialLoad by remember { mutableStateOf(true) }
    var portalInstance: WebView? by remember { mutableStateOf(null) }
    var fileCallback: ValueCallback<Array<Uri>>? by remember { mutableStateOf(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val tempFile = java.io.File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            tempFile.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            fileCallback?.onReceiveValue(arrayOf(uri))
        } else {
            fileCallback?.onReceiveValue(null)
        }
        fileCallback = null
    }

    val contentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        fileCallback?.onReceiveValue(uris.toTypedArray())
        fileCallback = null
    }

    BackHandler(enabled = true) {
        if (portalInstance?.canGoBack() == true) {
            portalInstance?.goBack()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)
                    cookieManager.flush()

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        allowFileAccess = true
                        allowContentAccess = true
                        javaScriptCanOpenWindowsAutomatically = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_NO_CACHE
                        setSupportMultipleWindows(false)
                        mediaPlaybackRequiresUserGesture = false
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        userAgentString = userAgentString.replace("; wv", "").replace("Version/4.0", "")
                    }

                    setDownloadListener { downloadAddress, userAgent, contentDisposition, mimeType, _ ->
                        try {
                            val request = DownloadManager.Request(Uri.parse(downloadAddress))
                            request.setMimeType(mimeType)

                            val cookies = CookieManager.getInstance().getCookie(downloadAddress)
                            if (!cookies.isNullOrEmpty()) {
                                request.addRequestHeader("Cookie", cookies)
                            }
                            request.addRequestHeader("User-Agent", userAgent)

                            val fileName = URLUtil.guessFileName(downloadAddress, contentDisposition, mimeType)
                            request.setTitle(fileName)
                            request.setDescription("Downloading file...")
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

                            val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            dm.enqueue(request)

                            Toast.makeText(ctx, "Download started: $fileName", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Download failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, link: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, link, favicon)
                            CookieManager.getInstance().flush()
                        }

                        override fun onPageFinished(view: WebView?, loadAddress: String?) {
                            super.onPageFinished(view, loadAddress)
                            if (isInitialLoad) {
                                isInitialLoad = false
                            }
                            CookieManager.getInstance().flush()
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            CookieManager.getInstance().flush()
                            return false
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onShowFileChooser(
                            view: WebView?,
                            callback: ValueCallback<Array<Uri>>?,
                            params: FileChooserParams?
                        ): Boolean {
                            fileCallback?.onReceiveValue(null)
                            fileCallback = callback

                            val acceptTypes = params?.acceptTypes ?: arrayOf("*/*")
                            val mimeType = if (acceptTypes.isNotEmpty() && !acceptTypes[0].isNullOrEmpty()) {
                                acceptTypes[0]
                            } else {
                                "*/*"
                            }

                            if (mimeType.startsWith("image/") && params?.isCaptureEnabled == true) {
                                cameraLauncher.launch(null)
                            } else {
                                contentPicker.launch(mimeType)
                            }
                            return true
                        }
                    }

                    portalInstance = this
                    loadUrl(address)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isInitialLoad) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun hideSystemBars(activity: Activity?) {
    activity ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        activity.window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
    }
}

@Suppress("DEPRECATION")
private fun showSystemBars(activity: Activity?) {
    activity ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
    } else {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}
