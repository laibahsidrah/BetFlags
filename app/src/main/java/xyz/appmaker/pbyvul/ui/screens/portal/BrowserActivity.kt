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

class MainBrowser : ComponentActivity() {

    companion object {
        const val PARAM_LINK = "url"
        private const val SELECTOR_REQUEST = 1001
    }

    private lateinit var browserView: WebView
    private lateinit var progressIndicator: ProgressBar
    private var uploadResponder: ValueCallback<Array<Uri>>? = null
    private var initialLoadCompleted = true

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        val startPage = intent.getStringExtra(PARAM_LINK) ?: run {
            finish()
            return
        }

        setupInterface()
        prepareBrowserEngine()
        navigateToPage(startPage)
        registerBackHandler()
    }

    private fun setupInterface() {
        val rootLayout = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        browserView = WebView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            id = View.generateViewId()
        }

        progressIndicator = ProgressBar(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            visibility = View.VISIBLE
        }

        rootLayout.addView(browserView)
        rootLayout.addView(progressIndicator)
        setContentView(rootLayout)

        activateImmersiveMode()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun prepareBrowserEngine() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(browserView, true)
        }

        browserView.apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }

        browserView.settings.apply {
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

        browserView.setDownloadListener { link, agent, disposition, mime, _ ->
            initiateFileRetrieval(link, agent, disposition, mime)
        }

        browserView.webViewClient = buildNavigationHandler()
        browserView.webChromeClient = buildInteractionHandler()
    }

    private fun buildNavigationHandler(): WebViewClient {
        return object : WebViewClient() {
            override fun onPageStarted(view: WebView?, address: String?, icon: android.graphics.Bitmap?) {
                super.onPageStarted(view, address, icon)
                if (initialLoadCompleted) {
                    progressIndicator.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, address: String?) {
                super.onPageFinished(view, address)
                if (initialLoadCompleted) {
                    initialLoadCompleted = false
                    progressIndicator.visibility = View.GONE
                }
                CookieManager.getInstance().flush()
            }

            override fun doUpdateVisitedHistory(view: WebView?, address: String?, refreshed: Boolean) {
                CookieManager.getInstance().flush()
                super.doUpdateVisitedHistory(view, address, refreshed)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val targetUri = request?.url ?: return false
                val uriScheme = targetUri.scheme ?: return false

                if (uriScheme in listOf("http", "https")) {
                    return false
                }

                return try {
                    val actionIntent = if (uriScheme == "intent") {
                        Intent.parseUri(targetUri.toString(), Intent.URI_INTENT_SCHEME)
                    } else {
                        Intent(Intent.ACTION_VIEW, targetUri)
                    }

                    launchExternalApp(view?.context ?: return true, actionIntent)
                    true
                } catch (exception: Exception) {
                    true
                }
            }

            override fun onRenderProcessGone(view: WebView, crashDetails: RenderProcessGoneDetail): Boolean {
                if (!isFinishing && !isDestroyed) {
                    recreate()
                }
                return true
            }
        }
    }

    private fun buildInteractionHandler(): WebChromeClient {
        return object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?,
                responder: ValueCallback<Array<Uri>>?,
                selectionParams: FileChooserParams?
            ): Boolean {
                uploadResponder?.onReceiveValue(null)
                uploadResponder = responder

                val acceptedFormats = selectionParams?.acceptTypes ?: arrayOf("*/*")
                val primaryFormat = acceptedFormats.firstOrNull()?.takeIf { it.isNotEmpty() } ?: "*/*"

                val pickIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = primaryFormat
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }

                return try {
                    startActivityForResult(pickIntent, SELECTOR_REQUEST)
                    true
                } catch (error: ActivityNotFoundException) {
                    Toast.makeText(this@MainBrowser, "Файловый менеджер не найден", Toast.LENGTH_SHORT).show()
                    responder?.onReceiveValue(null)
                    uploadResponder = null
                    false
                }
            }
        }
    }

    private fun navigateToPage(address: String) {
        browserView.loadUrl(address)
    }

    private fun registerBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (browserView.canGoBack()) {
                    browserView.goBack()
                }
            }
        })
    }

    private fun initiateFileRetrieval(
        sourceUrl: String,
        clientAgent: String,
        contentHeader: String,
        fileType: String
    ) {
        try {
            val downloadRequest = DownloadManager.Request(Uri.parse(sourceUrl))
            downloadRequest.setMimeType(fileType)

            val storedCookies = CookieManager.getInstance().getCookie(sourceUrl)
            if (!storedCookies.isNullOrEmpty()) {
                downloadRequest.addRequestHeader("Cookie", storedCookies)
            }
            downloadRequest.addRequestHeader("User-Agent", clientAgent)

            val suggestedName = URLUtil.guessFileName(sourceUrl, contentHeader, fileType)
            downloadRequest.setTitle(suggestedName)
            downloadRequest.setDescription("Downloading file...")
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, suggestedName)

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(downloadRequest)

            Toast.makeText(this, "Загрузка начата: $suggestedName", Toast.LENGTH_SHORT).show()
        } catch (error: Exception) {
            Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchExternalApp(context: Context, action: Intent): Boolean {
        return try {
            if (context !is ComponentActivity) {
                action.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(action)
            true
        } catch (error: Exception) {
            false
        }
    }

    @Suppress("DEPRECATION")
    private fun activateImmersiveMode() {
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
    private fun deactivateImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, responseData: Intent?) {
        super.onActivityResult(requestCode, resultCode, responseData)

        if (requestCode == SELECTOR_REQUEST) {
            processSelectionResult(resultCode, responseData)
        }
    }

    private fun processSelectionResult(statusCode: Int, selectionData: Intent?) {
        if (statusCode == RESULT_OK) {
            val collectedUris = mutableListOf<Uri>()
            
            selectionData?.data?.let { singleUri -> collectedUris.add(singleUri) }
            
            selectionData?.clipData?.let { multiClip ->
                for (index in 0 until multiClip.itemCount) {
                    multiClip.getItemAt(index).uri?.let { itemUri -> collectedUris.add(itemUri) }
                }
            }
            
            uploadResponder?.onReceiveValue(collectedUris.toTypedArray())
        } else {
            uploadResponder?.onReceiveValue(null)
        }
        
        uploadResponder = null
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }
}