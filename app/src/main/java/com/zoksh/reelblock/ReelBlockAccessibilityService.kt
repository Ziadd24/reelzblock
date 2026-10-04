package com.zoksh.reelblock

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ReelBlockAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastBlockAt = 0L

    companion object {
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
        private const val COOLDOWN_MS = 700L

        fun isEnabled(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
                    as android.view.accessibility.AccessibilityManager
            return manager.isEnabled
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            packageNames = arrayOf(INSTAGRAM_PACKAGE)
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.packageName?.toString() != INSTAGRAM_PACKAGE) return

        val now = System.currentTimeMillis()
        if (now - lastBlockAt < COOLDOWN_MS) return

        // Strong signal: user explicitly clicked something labelled Reels.
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            val clickedText = buildString {
                event.text?.forEach { append(it).append(' ') }
                event.contentDescription?.let { append(it) }
            }
            if (containsReelsSignal(clickedText)) {
                block()
                return
            }
        }

        // Secondary signal: a new Instagram window/content tree contains
        // a strong Reels label. This is intentionally conservative.
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            val root = rootInActiveWindow ?: return
            if (looksLikeReelsScreen(root)) {
                block()
            }
        }
    }

    private fun looksLikeReelsScreen(root: AccessibilityNodeInfo): Boolean {
        var reelsLabelCount = 0
        var videoActionCount = 0

        fun walk(node: AccessibilityNodeInfo?) {
            if (node == null) return

            val text = node.text?.toString()?.trim().orEmpty()
            val desc = node.contentDescription?.toString()?.trim().orEmpty()
            val combined = "$text $desc"

            if (containsExactReelsSignal(combined)) reelsLabelCount++
            if (containsVideoActionSignal(combined)) videoActionCount++

            for (i in 0 until node.childCount) {
                walk(node.getChild(i))
            }
        }

        walk(root)

        // Require multiple signals to reduce false positives from the home feed.
        return reelsLabelCount >= 1 && videoActionCount >= 1
    }

    private fun containsReelsSignal(value: String): Boolean {
        val s = value.lowercase()
        return s.contains("reels") || s.contains("reel")
    }

    private fun containsExactReelsSignal(value: String): Boolean {
        val s = value.lowercase()
        return Regex("""\breels?\b""").containsMatchIn(s)
    }

    private fun containsVideoActionSignal(value: String): Boolean {
        val s = value.lowercase()
        return s.contains("like") ||
                s.contains("comment") ||
                s.contains("share") ||
                s.contains("audio") ||
                s.contains("follow")
    }

    private fun block() {
        lastBlockAt = System.currentTimeMillis()
        handler.post {
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    override fun onInterrupt() = Unit
}
