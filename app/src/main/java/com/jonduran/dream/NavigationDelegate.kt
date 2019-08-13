package com.jonduran.dream

import androidx.activity.OnBackPressedCallback
import org.mozilla.geckoview.GeckoSession

class NavigationDelegate(
    private val onBackPressed: (Boolean) -> Unit = { _ -> },
    private val onLocationChanged: (GeckoSession, String?) -> Unit = { _, _ -> }
) : OnBackPressedCallback(true), GeckoSession.NavigationDelegate {
    private var _canGoBack = false
    private var _canGoForward = false

    override fun handleOnBackPressed() {
        onBackPressed(_canGoBack)
    }

    override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
        _canGoBack = canGoBack
    }

    override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
        _canGoForward = canGoForward
    }

    override fun onLocationChange(session: GeckoSession, url: String?) {
        onLocationChanged(session, url)
    }

    fun canGoBack(): Boolean = _canGoBack
    fun canGoForward(): Boolean = _canGoForward
}