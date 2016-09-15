package xyz.fcampbell.chatheads

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import org.jetbrains.anko.windowManager
import xyz.fcampbell.chatheads.view.ChatHeadLayout

/**
 * The main Service to manage chat heads.
 */
class ChatHeadService : Service() {
    private val layoutParams = WindowManager.LayoutParams().apply {
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    }

    private val chatHeads = mutableListOf<ChatHeadLayout>()

    /**
     * Adds a ChatHeadLayout as a floating view. The same instance of a ChatHeadLayout can only be added once.
     *
     * @param view The chat head to add.
     * @return Whether the chat head was successfully added or not.
     */
    fun addChatHead(view: ChatHeadLayout): Boolean {
        if (view in chatHeads) return false

        windowManager.addView(view, layoutParams)
        chatHeads += view
        return true
    }

    /**
     * Removes an already-added ChatHeadLayout.
     *
     * @param view The chat head to remove.
     * @return Whether the chat head was successfully removed or not.
     */
    fun removeChatHead(view: ChatHeadLayout): Boolean {
        if (view !in chatHeads) return false

        windowManager.removeView(view)
        chatHeads -= view
        return true
    }

    private val binder by lazy { LocalBinder() }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        /**
         * Retrieves the instance of ChatHeadService
         */
        val service = this@ChatHeadService
    }
}