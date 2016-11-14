package xyz.fcampbell.chatheads

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Binder
import android.os.IBinder
import android.support.annotation.StyleRes
import android.support.v7.view.ContextThemeWrapper
import xyz.fcampbell.chatheads.view.ChatHeadView
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * The main Service to manage chat heads.
 */
class ChatHeadService : Service() {
    private var chatHeadView: ChatHeadView? = null

    /**
     * Shows a ChatHeadView as a floating view.
     *
     * @return A WeakReference to the created ChatHeadView for convenience.
     * It is not strictly required to use the returned reference.
     * This class keeps a hard reference to the view until it is detached.
     */
    @JvmOverloads
    fun initialize(adapter: ChatHeadAdapter, @StyleRes themeResId: Int = 0) {
        detachView()

        if (themeResId != 0) setTheme(themeResId)
        initializeView(adapter, this)
    }

    fun initialize(adapter: ChatHeadAdapter, theme: Resources.Theme) {
        detachView()

        initializeView(adapter, ContextThemeWrapper(this, theme))
    }

    private fun initializeView(adapter: ChatHeadAdapter, context: Context) {
        chatHeadView = ChatHeadView(context, adapter).apply {
            onTrashListener = { detachView() }
            attachToWindow()
        }
    }

    /**
     * Removes an already-added ChatHeadView.
     */
    fun detachView() {
        chatHeadView?.detachFromWindow()
        chatHeadView = null
    }

    /**
     * Opens the chat head panel.
     */
    fun openChatHeads() = chatHeadView?.open()

    /**
     * Closes the chat head panel to thumbnail form
     */
    fun closeChatHeads() = chatHeadView?.close()

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