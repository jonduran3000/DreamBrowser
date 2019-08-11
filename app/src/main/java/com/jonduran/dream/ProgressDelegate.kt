package com.jonduran.dream

import org.mozilla.geckoview.GeckoSession

class ProgressDelegate(
    private val onProgressChanged: (GeckoSession, Int) -> Unit = { _, _ -> }
) : GeckoSession.ProgressDelegate {
    override fun onProgressChange(session: GeckoSession, progress: Int) {
        onProgressChanged(session, progress)
    }
}