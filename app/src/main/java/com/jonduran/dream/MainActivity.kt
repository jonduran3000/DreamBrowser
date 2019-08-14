package com.jonduran.dream

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.net.toUri
import androidx.core.view.isGone
import kotlinx.android.synthetic.main.activity_main.*
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings

class MainActivity : AppCompatActivity() {
    private lateinit var runtime: GeckoRuntime
    private val settings = GeckoSessionSettings.Builder()
        .usePrivateMode(true)
        .useTrackingProtection(true)
        .userAgentMode(GeckoSessionSettings.USER_AGENT_MODE_MOBILE)
        .userAgentOverride("")
        .suspendMediaWhenInactive(true)
        .allowJavascript(true)
        .build()
    private val session = GeckoSession(settings)

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
            }
        )

        val contentBlocking = ContentBlockingDelegate()
        setUpWebView(navigation, contentBlocking)
        setUpToolbar(navigation, contentBlocking)
        onBackPressedDispatcher.addCallback(this, navigation)
    }

    private fun setUpWebView(
        navigationDelegate: GeckoSession.NavigationDelegate,
        contentBlockingDelegate: ContentBlockingDelegate
    ) {
        runtime = GeckoRuntime.create(this)
        session.open(runtime)
        webView.setSession(session)
        session.navigationDelegate = navigationDelegate
        session.progressDelegate = ProgressDelegate(
            onPageStarted = { _, url ->
                contentBlockingDelegate.clear()
                input.setText(url, TextView.BufferType.EDITABLE)
            },
            onProgressChanged = { _, progress ->
                progressIndicator.progress = progress
                progressIndicator.isGone = progress >= 100
            }
        )
        session.contentBlockingDelegate = contentBlockingDelegate
        session.loadUri("about:blank")
    }


    private fun setUpToolbar(
        navigation: NavigationDelegate,
        contentBlocking: ContentBlockingDelegate
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        input.setOnEditorActionListener(onEditorActionListener)

        val popup = PopupWindow(this)
        popup.isFocusable = true
        popup.isOutsideTouchable = true

        @SuppressLint("InflateParams")
        val content = layoutInflater.inflate(R.layout.nav_action_layout, null)

        menuButton.setOnClickListener { view ->
            showPopup(
                navigation = navigation,
                contentBlocking = contentBlocking,
                popup = popup,
                contentView = content,
                anchorView = view
            )
        }
    }

    private fun showPopup(
        navigation: NavigationDelegate,
        contentBlocking: ContentBlockingDelegate,
        popup: PopupWindow,
        contentView: View,
        anchorView: View
    ) {
        val goBackButton = contentView.findViewById<AppCompatImageButton>(R.id.goBackButton)
        goBackButton.isEnabled = navigation.canGoBack()
        goBackButton.setOnClickListener {
            session.goBack()
            popup.dismiss()
        }

        val goForwardButton = contentView.findViewById<AppCompatImageButton>(R.id.goForwardButton)
        goForwardButton.isEnabled = navigation.canGoForward()
        goForwardButton.setOnClickListener {
            session.goForward()
            popup.dismiss()
        }

        val refreshButton = contentView.findViewById<AppCompatImageButton>(R.id.refreshButton)
        refreshButton.setOnClickListener {
            session.reload()
            popup.dismiss()
        }

        val blockedTrackersLabel = contentView.findViewById<TextView>(R.id.blockedTrackers)
        blockedTrackersLabel.text = getString(
            R.string.blocked_trackers,
            contentBlocking.getBlockedEvents().size
        )

        popup.contentView = contentView
        popup.width = ViewGroup.LayoutParams.WRAP_CONTENT
        popup.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popup.showAsDropDown(anchorView, anchorView.right, anchorView.top, Gravity.NO_GRAVITY)
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
