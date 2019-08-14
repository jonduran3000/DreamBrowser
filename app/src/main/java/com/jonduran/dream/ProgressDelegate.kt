package com.jonduran.dream

import org.mozilla.geckoview.GeckoSession

class ProgressDelegate(
    private val onPageStarted: (GeckoSession, String) -> Unit = { _, _ -> },
    private val onProgressChanged: (GeckoSession, Int) -> Unit = { _, _ -> }
) : GeckoSession.ProgressDelegate {
    override fun onPageStart(session: GeckoSession, url: String) {
        onPageStarted(session, url)
    }

    override fun onProgressChange(session: GeckoSession, progress: Int) {
        onProgressChanged(session, progress)
    }
}