package com.jonduran.dream

import androidx.activity.OnBackPressedCallback
import org.mozilla.geckoview.GeckoSession

class NavigationDelegate(
    private val onBackPressed: (Boolean) -> Unit = { _ -> },
    private val onLocationChanged: (GeckoSession, String?) -> Unit = { _, _ -> }
) : OnBackPressedCallback(true), GeckoSession.NavigationDelegate {
    private var canGoBack = false

    override fun handleOnBackPressed() {
        onBackPressed(canGoBack)
    }

    override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
        this.canGoBack = canGoBack
    }

    override fun onLocationChange(session: GeckoSession, url: String?) {
        onLocationChanged(session, url)
    }
}