package xyz.appmaker.pbyvul.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import xyz.appmaker.pbyvul.MainActivity
import xyz.appmaker.pbyvul.R
import xyz.appmaker.pbyvul.util.StorageHelper

class AccountRemovalActivity : AppCompatActivity() {

    private lateinit var contentPortal: WebView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var closeButton: ImageButton

    private val removalPattern = Regex("delete-user-app-yes")
    private val closeDelayMs = 2000L
    private var removalDetected = false
    private var canCheckRemoval = false
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultData = if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { arrayOf(it) }
        } else null
        filePathCallback?.onReceiveValue(resultData)
        filePathCallback = null
    }

    companion object {
        private const val REMOVAL_BASE = "https://getapisportapp.site/bf-app/delete-user-app/"

        fun start(context: Context) {
            val intent = Intent(context, AccountRemovalActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CookieManager.getInstance().setAcceptCookie(true)

        setContentView(R.layout.activity_account_removal)

        contentPortal = findViewById(R.id.contentPortal)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        closeButton = findViewById(R.id.closeButton)

        setupContentPortal()
        setupCloseButton()
        setupBackNavigation()
        openRemovalPage()

        Handler(Looper.getMainLooper()).postDelayed({
            canCheckRemoval = true
        }, 1500)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupContentPortal() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(contentPortal, true)
        }

        contentPortal.apply {
            setBackgroundColor(Color.BLACK)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE
                allowFileAccess = true
                allowContentAccess = true
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                setSupportZoom(true)
                setSupportMultipleWindows(false)
                userAgentString = userAgentString.replace("; wv", "").replace("Version/4.0", "")
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, link: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, link, favicon)
                    loadingIndicator.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, link: String?) {
                    super.onPageFinished(view, link)
                    loadingIndicator.visibility = View.GONE
                    CookieManager.getInstance().flush()
                    link?.let { checkForRemovalConfirmation(it) }
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    request?.url?.toString()?.let { checkForRemovalConfirmation(it) }
                    return false
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) {
                        loadingIndicator.visibility = View.GONE
                    }
                }

                override fun onShowFileChooser(
                    portal: WebView?,
                    callback: ValueCallback<Array<Uri>>?,
                    params: FileChooserParams?
                ): Boolean {
                    filePathCallback?.onReceiveValue(null)
                    filePathCallback = callback
                    val intent = params?.createIntent()
                    if (intent != null) {
                        fileChooserLauncher.launch(intent)
                    } else {
                        callback?.onReceiveValue(null)
                    }
                    return true
                }
            }
        }
    }

    private fun checkForRemovalConfirmation(destination: String) {
        if (!canCheckRemoval || removalDetected) {
            return
        }

        if (removalPattern.containsMatchIn(destination)) {
            removalDetected = true
            closeButton.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                clearAllAppData()
                navigateToMainScreen()
            }, closeDelayMs)
        }
    }

    private fun clearAllAppData() {
        StorageHelper(this).clearAll()
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupCloseButton() {
        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (removalDetected) {
                    return
                }
                if (contentPortal.canGoBack()) {
                    contentPortal.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun openRemovalPage() {
        val storage = StorageHelper(this)
        val countryCode = storage.getSavedCountryCode() ?: ""
        val phone = storage.getSavedPhone() ?: ""
        val fullPhone = "$countryCode$phone"

        val destination = if (fullPhone.isNotEmpty()) {
            "$REMOVAL_BASE?phone=${java.net.URLEncoder.encode(fullPhone, "UTF-8")}"
        } else {
            REMOVAL_BASE
        }

        contentPortal.loadUrl(destination)
    }

    override fun onResume() {
        super.onResume()
        contentPortal.onResume()
        CookieManager.getInstance().setAcceptCookie(true)
    }

    override fun onPause() {
        super.onPause()
        contentPortal.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onDestroy() {
        CookieManager.getInstance().flush()
        contentPortal.apply {
            stopLoading()
            clearHistory()
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }
        super.onDestroy()
    }
}
