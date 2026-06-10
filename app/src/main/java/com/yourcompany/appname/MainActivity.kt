package com.yourcompany.appname

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.yourcompany.appname.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ✏️ CHANGE: Your website URL
    private val webUrl = "https://your-website.com/"

    // ✏️ CHANGE: Your share message
    private val shareMessage = "Check out this app!\nhttps://play.google.com/store/apps/details?id=com.yourcompany.appname"

    // ✏️ CHANGE: Your website host (for network disconnect detection)
    private val webHost = "your-website.com"

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!isNetworkAvailable()) {
                startActivity(Intent(this@MainActivity, NoNetworkActivity::class.java))
                finish()
            }
        }
    }

    inner class AndroidBridge(private val context: Context) {
        @JavascriptInterface
        fun share() { runOnUiThread { shareApp() } }

        @JavascriptInterface
        fun openUrl(url: String) { runOnUiThread { openCustomTab(url) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWebView()
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
            }

            addJavascriptInterface(AndroidBridge(this@MainActivity), "Android")

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val url = request.url.toString()
                    return when {
                        url.startsWith("app://share") -> { shareApp(); true }
                        url.contains(webHost) -> false
                        url.startsWith("http") -> { openCustomTab(url); true }
                        else -> false
                    }
                }
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.progressBar.show()
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.hide()
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                }
            }

            loadUrl(webUrl)
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun openCustomTab(url: String) {
        try {
            val params = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.primary))
                .build()
            CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(params)
                .setShowTitle(true)
                .build()
                .launchUrl(this, Uri.parse(url))
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else moveTaskToBack(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(networkReceiver) } catch (e: Exception) {}
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        return cm.getNetworkCapabilities(net)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
