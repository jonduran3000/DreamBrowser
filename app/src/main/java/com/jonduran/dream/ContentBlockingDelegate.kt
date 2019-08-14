package com.jonduran.dream

import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoSession

class ContentBlockingDelegate : ContentBlocking.Delegate {
    private val blockedEvents = mutableListOf<ContentBlocking.BlockEvent>()
    override fun onContentBlocked(session: GeckoSession, event: ContentBlocking.BlockEvent) {
        if (event.isBlocking) {
            blockedEvents.add(event)
        }
    }

    fun getBlockedEvents(): List<ContentBlocking.BlockEvent> = blockedEvents

    fun clear() {
        blockedEvents.clear()
    }
}