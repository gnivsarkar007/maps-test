/*
 * Copyright (c) 2019.
 * Bismillahir Rahmanir Rahim,
 * Developer : Saadat Sayem
 */

package com.example.mapstest.svg

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mapstest.R
import com.example.mapstest.nativemap.showToast
import com.sam43.svginteractiondemo.getHTMLBody
import com.sam43.svginteractiondemo.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SVGMapActivity : AppCompatActivity() {

    companion object {
        const val JAVASCRIPT_OBJ = "javascript_obj"
        const val BASE_URL = "file:///android_asset/web/"
    }

    private lateinit var fileDownloaderVM: FileDownloaderVM
    private lateinit var pd: ProgressDialog

    lateinit var webView: WebView
    lateinit var btnZoomIn: View
    lateinit var btnZoomOut: View
    lateinit var overlay: View


    private fun initProgressDialog() {
        pd = ProgressDialog(this)
        pd.setCancelable(false)
        pd.isIndeterminate = true
        pd.setTitle("Rendering SVG")
        pd.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initVM()
        setupButtonActions()
        setupWebLayout()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun setupButtonActions() {
        initProgressDialog()
        webView = findViewById<WebView>(R.id.webView)
        btnZoomIn = findViewById<View>(R.id.btnZoomIn)
        btnZoomOut = findViewById<View>(R.id.btnZoomOut)
        overlay = findViewById(R.id.webViewOverlay)
        btnZoomIn.setOnClickListener { webView.zoomIn() }
        btnZoomOut.setOnClickListener { webView.zoomOut() }
//        webView.settings.forceDark = FORCE_DARK_ON
//        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
//            WebSettingsCompat.setForceDarkStrategy(webView.settings, FORCE_DARK_ON)
//        }
//        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
//            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//                Configuration.UI_MODE_NIGHT_YES -> {
//                    WebSettingsCompat.setForceDark(webView.settings, FORCE_DARK_ON)
//                }
//                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
//                    WebSettingsCompat.setForceDark(webView.settings, FORCE_DARK_OFF)
//                }
//                else -> {
//                    //
//                }
//            }
//        }

        webView.setOnTouchListener { a, b ->
            if (b.actionMasked == MotionEvent.ACTION_DOWN) {
                lifecycleScope.launch {
                    delay(200L)
                    showPopupWindow(a, b)
                }
            } else {
                if (popupWindow?.isShowing == true) {
                    popupWindow?.dismiss()
                    popupWindow = null
                }
            }
//            if (b.action == MotionEvent.ACTION_UP) {
////                val rect = Rect()
////                val locationRect = a.getGlobalVisibleRect(rect)
////                rect.
////                val(x, y) = b.
//
//            }
            false
        }


//        sendToWeb.setOnClickListener {
//            webView.evaluateJavascript(
//                "javascript: " +
//                        "updateFromAndroid(\"" + sendDataField.text + "\")",
//                object : ValueCallback<String> {
//                    override fun onReceiveValue(p0: String?) {
//                        showToast(p0 ?: "Empty message")
//                    }
//                }
//            )
//        }
    }

    var popupWindow: PopupWindow? = null

    private fun showPopupWindow(a: View?, b: MotionEvent) {
        if (popupWindow?.isShowing == true) {
            popupWindow?.dismiss()
            popupWindow = null
        }
        if (stateName.value == "undefined" || stateName.value.isEmpty()) {
            return
        }
        popupWindow = PopupWindow(this@SVGMapActivity).apply {
            contentView =
                TextView(this@SVGMapActivity).apply { id = R.id.genView; text = stateName.value }
            showAtLocation(a, Gravity.NO_GRAVITY, b.rawX.toInt(), b.rawY.toInt())
        }
    }

    private fun initVM() {
        fileDownloaderVM = ViewModelProvider(this).get(FileDownloaderVM::class.java)
    }

    override fun onResume() {
        super.onResume()
        callVM()
    }

    private fun callVM() {
        val url = "https://svgshare.com/i/Gzd.svg"
        lifecycleScope.launch {
            fileDownloaderVM.readXML(resources.openRawResource(R.raw.usa))?.let {
                webView.loadDataWithBaseURL(
                    BASE_URL, getHTMLBody(it), "text/html",
                    "UTF-8", null
                )
            }
            pd.dismiss()
        }
//        try {
//            fileDownloaderVM.downloadFileFromServer(url)
//                .observe(this) { responseBody ->
//                    val svgString = responseBody.string()
//
//                }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }


    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    private fun setupWebLayout() {
        webView.setInitialScale(90)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(
            JavaScriptInterface(),
            JAVASCRIPT_OBJ
        )
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectJavaScriptFunction()
            }
        }
        webView.webChromeClient = WebChromeClient()
    }

    private fun injectJavaScriptFunction() {
        val myNumber = "{ \"code\": \"Code_CA\" }"
        val textToAndroid = "javascript: window.androidObj.textToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".textFromWeb(message) }"
        webView.loadUrl(textToAndroid)
        webView.evaluateJavascript(
            "javascript: " +
                    "initialiseMap(" + myNumber + ")",
            object : ValueCallback<String> {
                override fun onReceiveValue(p0: String?) {
                    showToast(p0 ?: "Empty message")
                }
            }
        )
    }

    val stateName = mutableStateOf("")
    inner class JavaScriptInterface {
        @SuppressLint("SetTextI18n")
        @JavascriptInterface
        fun textFromWeb(fromWeb: String) {
            runOnUiThread {
                stateName.value = fromWeb

            }
            toast(fromWeb)
        }
        @JavascriptInterface
        fun getJSONData(): String = "{ \"code\": \"CODE_CA\" }"
    }

    override fun onDestroy() {
        webView.removeJavascriptInterface(JAVASCRIPT_OBJ)
        super.onDestroy()
    }
}


