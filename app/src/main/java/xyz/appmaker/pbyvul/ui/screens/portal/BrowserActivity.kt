package xyz.appmaker.pbyvul.ui.screens.portal

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.URLUtil
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import android.webkit.RenderProcessGoneDetail
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback

class BrowserActivity : ComponentActivity() {

    companion object {
        const val EXTRA_URL = "url"
        private const val FILE_PICKER_REQUEST = 1001
    }

    private lateinit var webView: WebView
    private lateinit var loadingSpinner: ProgressBar
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private var isFirstPageLoad = true

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        val targetUrl = intent.getStringExtra(EXTRA_URL) ?: run {
            finish()
            return
        }

        initializeUi()
        configureWebView()
        loadInitialPage(targetUrl)
        setupBackNavigation()
    }

    private fun initializeUi() {
        val container = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        webView = WebView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            id = View.generateViewId()
        }

        loadingSpinner = ProgressBar(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            visibility = View.VISIBLE
        }

        container.addView(webView)
        container.addView(loadingSpinner)
        setContentView(container)

        enableFullscreenMode()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        webView.apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            loadWithOverviewMode = false
            useWideViewPort = true
            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(false)
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false
            loadsImagesAutomatically = true
            blockNetworkImage = false
            setSupportMultipleWindows(false)
            safeBrowsingEnabled = false
            userAgentString = userAgentString.replace("; wv", "").replace("Version/4.0 ", "")
        }

        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            handleFileDownload(url, userAgent, contentDisposition, mimeType)
        }

        webView.webViewClient = createWebViewClient()
        webView.webChromeClient = createWebChromeClient()
    }

    private fun createWebViewClient(): WebViewClient {
        return object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (isFirstPageLoad) {
                    loadingSpinner.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (isFirstPageLoad) {
                    isFirstPageLoad = false
                    loadingSpinner.visibility = View.GONE
                }
                CookieManager.getInstance().flush()
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                CookieManager.getInstance().flush()
                super.doUpdateVisitedHistory(view, url, isReload)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                val scheme = uri.scheme ?: return false

                if (scheme in listOf("http", "https")) {
                    return false
                }

                return try {
                    val intent = if (scheme == "intent") {
                        Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
                    } else {
                        Intent(Intent.ACTION_VIEW, uri)
                    }

                    openExternalIntent(view?.context ?: return true, intent)
                    true
                } catch (e: Exception) {
                    true
                }
            }

            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                if (!isFinishing && !isDestroyed) {
                    recreate()
                }
                return true
            }
        }
    }

    private fun createWebChromeClient(): WebChromeClient {
        return object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: FileChooserParams?
            ): Boolean {
                fileUploadCallback?.onReceiveValue(null)
                fileUploadCallback = callback

                val acceptTypes = params?.acceptTypes ?: arrayOf("*/*")
                val mimeType = acceptTypes.firstOrNull()?.takeIf { it.isNotEmpty() } ?: "*/*"

                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = mimeType
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }

                return try {
                    startActivityForResult(intent, FILE_PICKER_REQUEST)
                    true
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this@BrowserActivity, "Файловый менеджер не найден", Toast.LENGTH_SHORT).show()
                    callback?.onReceiveValue(null)
                    fileUploadCallback = null
                    false
                }
            }
        }
    }

    private fun loadInitialPage(url: String) {
        webView.loadUrl(url)
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        })
    }

    private fun handleFileDownload(
        downloadUrl: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String
    ) {
        try {
            val request = DownloadManager.Request(Uri.parse(downloadUrl))
            request.setMimeType(mimeType)

            val cookies = CookieManager.getInstance().getCookie(downloadUrl)
            if (!cookies.isNullOrEmpty()) {
                request.addRequestHeader("Cookie", cookies)
            }
            request.addRequestHeader("User-Agent", userAgent)

            val fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType)
            request.setTitle(fileName)
            request.setDescription("Downloading file...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(this, "Загрузка начата: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openExternalIntent(context: Context, intent: Intent): Boolean {
        return try {
            if (context !is ComponentActivity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    @Suppress("DEPRECATION")
    private fun enableFullscreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (
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
    private fun disableFullscreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST) {
            handleFilePickerResult(resultCode, data)
        }
    }

    private fun handleFilePickerResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val uris = mutableListOf<Uri>()
            
            data?.data?.let { uri -> uris.add(uri) }
            
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    clipData.getItemAt(i).uri?.let { uri -> uris.add(uri) }
                }
            }
            
            fileUploadCallback?.onReceiveValue(uris.toTypedArray())
        } else {
            fileUploadCallback?.onReceiveValue(null)
        }
        
        fileUploadCallback = null
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }
}