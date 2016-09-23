package xyz.fcampbell.chatheads

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.annotation.StyleRes
import xyz.fcampbell.chatheads.view.ChatHeadView
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * The main Service to manage chat heads.
 */
class ChatHeadService : Service() {
    private lateinit var chatHeadView: ChatHeadView
    private var attachedToWindow = false

    /**
     * Shows a ChatHeadView as a floating view.
     *
     * @return The ChatHeadView for convenience. It is not strictly required to use the returned reference.
     */
    @JvmOverloads
    fun initialize(adapter: ChatHeadAdapter, @StyleRes themeResId: Int = 0): ChatHeadView {
        if (attachedToWindow) return chatHeadView //already attached

        if (themeResId != 0) setTheme(themeResId)

        chatHeadView = ChatHeadView(this)
        chatHeadView.initialize(adapter, { detachView() })
        chatHeadView.attachToWindow()

        attachedToWindow = true
        return chatHeadView
    }

    /**
     * Removes an already-added ChatHeadView.
     */
    fun detachView() {
        if (attachedToWindow) {
            chatHeadView.detachFromWindow()
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