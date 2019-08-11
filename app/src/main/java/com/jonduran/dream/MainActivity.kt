package com.jonduran.dream

import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isGone
import kotlinx.android.synthetic.main.activity_main.*
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings

class MainActivity : AppCompatActivity() {
    private lateinit var runtime: GeckoRuntime
    private val session = GeckoSession(
        GeckoSessionSettings.Builder()
            .usePrivateMode(true)
            .useTrackingProtection(true)
            .userAgentMode(GeckoSessionSettings.USER_AGENT_MODE_MOBILE)
            .userAgentOverride("")
            .suspendMediaWhenInactive(true)
            .allowJavascript(true)
            .build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = NavigationDelegate(
            onBackPressed = { canGoBack ->
                if (canGoBack) {
                    session.goBack()
                } else {
                    finish()
                }
            },
            onLocationChanged = { _, url ->
                input.setText(url, TextView.BufferType.EDITABLE)
            }
        )

        setUpWebView(navigation)
        setUpInput()
        onBackPressedDispatcher.addCallback(this, navigation)
    }

    private fun setUpWebView(navigationDelegate: GeckoSession.NavigationDelegate) {
        runtime = GeckoRuntime.create(this)
        session.open(runtime)
        webView.setSession(session)
        session.navigationDelegate = navigationDelegate
        session.progressDelegate = ProgressDelegate { _, progress ->
            progressIndicator.progress = progress
            progressIndicator.isGone = progress >= 100
        }
        session.loadUri("about:buildconfig")
    }

    private fun setUpInput() {
        setSupportActionBar(toolbar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        input.setOnEditorActionListener(onEditorActionListener)
    }

    private val onEditorActionListener = object : TextView.OnEditorActionListener {
        private val SEARCH_URI = "https://duckduckgo.com/".toUri()

        override fun onEditorAction(textView: TextView, actionId: Int, event: KeyEvent?): Boolean {
            commit(textView.text.toString())
            webView.requestFocus()
            textView.hideKeyboard()
            return true
        }

        private fun commit(text: String) {
            if (Patterns.WEB_URL.matcher(text).matches()) {
                session.loadUri(text)
            } else {
                performSearchQuery(text)
            }
        }

        private fun performSearchQuery(query: String) {
            val uri = SEARCH_URI.buildUpon()
                .appendQueryParameter("q", query)
                .build()
            session.loadUri(uri)
        }
    }
}
