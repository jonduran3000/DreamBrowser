package com.jonduran.dream

import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import org.mozilla.geckoview.GeckoSession

class OffsetScrollDelegate(private val target: View) : GeckoSession.ScrollDelegate {
    private val top = target.top
    private val bottom = -target.bottom
    private var lastYScrollPosition = 0
    private var lastXScrollPosition = 0
    private var offsetY = 0

    override fun onScrollChanged(session: GeckoSession, scrollX: Int, scrollY: Int) {
        val deltaX = lastXScrollPosition - scrollX
        val deltaY = lastYScrollPosition - scrollY

        lastXScrollPosition = scrollX
        lastYScrollPosition = scrollY

        offsetY += deltaY

        Log.i("MainActivity", "=====================")
        Log.i("MainActivity", "Top: $top")
        Log.i("MainActivity", "Bottom: $bottom")
        Log.i("MainActivity", "Scroll Y: $scrollY")
        Log.i("MainActivity", "Delta Y: $deltaY")
        Log.i("MainActivity", "Offset Y: $offsetY")

        if (offsetY > top) {
            offsetY = top
            Log.i("MainActivity", "=====================")
            return
        }
        if (offsetY < bottom) {
            offsetY = bottom
            Log.i("MainActivity", "=====================")
            return
        }

        ViewCompat.offsetTopAndBottom(target, deltaY)

        Log.i("MainActivity", "Updated Top: ${target.top}")
        Log.i("MainActivity", "Updated Bottom: ${target.bottom}")
        Log.i("MainActivity", "=====================")
    }
}