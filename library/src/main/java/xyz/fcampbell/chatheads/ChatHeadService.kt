package xyz.fcampbell.chatheads

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import org.jetbrains.anko.windowManager
import xyz.fcampbell.chatheads.view.ChatHeadView

/**
 * The main Service to manage chat heads.
 */
class ChatHeadService : Service() {
    private val layoutParams = WindowManager.LayoutParams().apply {
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    }

    var rootView: ChatHeadView? = null
        private set

    /**
     * Shows a ChatHeadView as a floating view.
     * @param view The root layout to show.
     */
    fun show(view: ChatHeadView) {
        if (rootView != null) {
            windowManager.removeView(rootView)
        }
        windowManager.addView(view, layoutParams)
        rootView = view
    }

    /**
     * Removes an already-added ChatHeadView.
     */
    fun hide() {
        if (rootView == null) return

        windowManager.removeView(rootView)
        rootView = null
    }

    override fun onDestroy() {
        super.onDestroy()

        hide()
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