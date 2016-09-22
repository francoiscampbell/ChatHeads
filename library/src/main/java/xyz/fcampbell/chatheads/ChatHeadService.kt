package xyz.fcampbell.chatheads

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import xyz.fcampbell.chatheads.view.ChatHeadView

/**
 * The main Service to manage chat heads.
 */
class ChatHeadService : Service() {
    val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    private val layoutParams = WindowManager.LayoutParams().apply {
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        format = PixelFormat.TRANSLUCENT
    }

    var rootView: ChatHeadView? = null
        private set

    /**
     * Shows a ChatHeadView as a floating view.
     * @param view The root layout to attachView.
     */
    fun attachView(view: ChatHeadView) {
        if (rootView != null) {
            windowManager.removeView(rootView)
        }
        windowManager.addView(view, layoutParams)
        rootView = view
    }

    /**
     * Removes an already-added ChatHeadView.
     */
    fun detachView() {
        if (rootView == null) return

        windowManager.removeView(rootView)
        rootView = null
    }

    /**
     * Opens the chat head panel
     */
    fun openChatHeads() = rootView?.open()

    /**
     * Closes the chat head panel to thumbnail form
     */
    fun closeChatHeads() = rootView?.close()

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