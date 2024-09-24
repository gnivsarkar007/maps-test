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
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var sendToWeb: View
    lateinit var sendDataField: EditText
    lateinit var stateName: TextView
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
        sendToWeb = findViewById<View>(R.id.btnSendToWeb)
        sendDataField = findViewById<EditText>(R.id.etSendDataField)
        stateName = findViewById<TextView>(R.id.tvStateName)
        overlay = findViewById(R.id.webViewOverlay)
        btnZoomIn.setOnClickListener { webView.zoomIn() }
        btnZoomOut.setOnClickListener { webView.zoomOut() }

        overlay.setOnTouchListener { a, b ->
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


        sendToWeb.setOnClickListener {
            webView.evaluateJavascript(
                "javascript: " +
                        "updateFromAndroid(\"" + sendDataField.text + "\")",
                object : ValueCallback<String> {
                    override fun onReceiveValue(p0: String?) {
                        showToast(p0 ?: "Empty message")
                    }
                }
            )
        }
    }

    var popupWindow: PopupWindow? = null

    private fun showPopupWindow(a: View?, b: MotionEvent) {
        if (popupWindow?.isShowing == true) {
            popupWindow?.dismiss()
            popupWindow = null
        }
        if (stateName.text == "undefined" || stateName.text.isNullOrEmpty()) {
            return
        }
        popupWindow = PopupWindow(this@SVGMapActivity).apply {
            contentView =
                TextView(this@SVGMapActivity).apply { id = R.id.genView; text = stateName.text }
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
        try {
            fileDownloaderVM.downloadFileFromServer(url)
                .observe(this) { responseBody ->
                    val svgString = responseBody.string()
                    webView.loadDataWithBaseURL(
                        BASE_URL, getHTMLBody(svgString), "text/html",
                        "UTF-8", null
                    )
                    pd.dismiss()
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    private fun setupWebLayout() {
        webView.setInitialScale(150)
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = true
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
        val textToAndroid = "javascript: window.androidObj.textToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".textFromWeb(message) }"
        webView.loadUrl(textToAndroid)
    }


    inner class JavaScriptInterface {
        @SuppressLint("SetTextI18n")
        @JavascriptInterface
        fun textFromWeb(fromWeb: String) {
            runOnUiThread {
                stateName.text = fromWeb

            }
            toast(fromWeb)
        }
    }

    override fun onDestroy() {
        webView.removeJavascriptInterface(JAVASCRIPT_OBJ)
        super.onDestroy()
    }
}
