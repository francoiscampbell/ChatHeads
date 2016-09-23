package xyz.fcampbell.chatheads

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.annotation.StyleRes
import android.view.WindowManager
import xyz.fcampbell.chatheads.view.ChatHeadView
import xyz.fcampbell.chatheads.view.FloatingChatHeadView
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * The main Service to manage chat heads.
 */
class ChatHeadService : Service() {
    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private val chatHeadView by lazy { FloatingChatHeadView(this) }

    private var attachedToWindow = false

    /**
     * Shows a ChatHeadView as a floating view.
     *
     * @return The ChatHeadView for convenience. It is not strictly required to use the returned reference.
     */
    @JvmOverloads
    fun initialize(adapter: ChatHeadAdapter, @StyleRes themeResId: Int = 0): ChatHeadView {
        if (themeResId != 0) setTheme(themeResId)
        attachedToWindow = true
        chatHeadView.initialize(adapter, { detachView() })
        chatHeadView.attachToWindow(windowManager)
        return chatHeadView
    }

    /**
     * Removes an already-added ChatHeadView.
     */
    fun detachView() {
        if (attachedToWindow) {
            windowManager.removeView(chatHeadView)
            attachedToWindow = false
        }
    }

    /**
     * Opens the chat head panel.
     */
    fun openChatHeads() {
        if (!attachedToWindow) {
            throw IllegalStateException("The ChatHeadView is not attached. Call initialize(...) first")
        }
        chatHeadView.open()
    }

    /**
     * Closes the chat head panel to thumbnail form
     */
    fun closeChatHeads() = chatHeadView.close()

    override fun onDestroy() {
        super.onDestroy()

        detachView()
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