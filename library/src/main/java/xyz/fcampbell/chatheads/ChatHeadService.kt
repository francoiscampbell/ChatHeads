package xyz.fcampbell.chatheads

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.WindowManager
import xyz.fcampbell.chatheads.view.ChatHeadView
import xyz.fcampbell.chatheads.view.FloatingChatHeadView

/**
 * The main Service to manage chat heads.
 */
class ChatHeadService : Service() {
    val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    fun createChatHeadView(activityContext: Context): ChatHeadView {
        floatingChatHeadView = FloatingChatHeadView(activityContext)
        return floatingChatHeadView
    }

    private lateinit var floatingChatHeadView: FloatingChatHeadView

    private var attachedToWindow = false

    /**
     * Shows a ChatHeadView as a floating view.
     */
    fun attachView() {
        attachedToWindow = true
        floatingChatHeadView.attachToWindow(windowManager)
    }

    /**
     * Removes an already-added ChatHeadView.
     */
    fun detachView() {
        if (attachedToWindow) {
            windowManager.removeView(floatingChatHeadView)
            attachedToWindow = false
        }
    }

    /**
     * Opens the chat head panel
     */
    fun openChatHeads() {
        if (!attachedToWindow) {
            attachView()
        }
        floatingChatHeadView.open()
    }

    /**
     * Closes the chat head panel to thumbnail form
     */
    fun closeChatHeads() = floatingChatHeadView.close()

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