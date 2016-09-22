package xyz.fcampbell.chatheads.view

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.WindowManager

/**
 * Customization of ChatHeadView that is meant to be shown in a floating system overlay
 */
class FloatingChatHeadView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ChatHeadView(
        context,
        attrs,
        defStyleAttr) {

    lateinit var windowManager: WindowManager
    private val layoutParams = WindowManager.LayoutParams().apply {
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        format = PixelFormat.TRANSLUCENT
    }

    fun attachToWindow(windowManager: WindowManager) {
        this.windowManager = windowManager
        windowManager.addView(this, layoutParams)
    }

    override fun savePosition() {
        oldX = layoutParams.x.toFloat()
        oldY = layoutParams.y.toFloat()
    }

    override fun setLayoutParamsForOpening() {
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE //does nothing???
    }

    override fun setLayoutParamsForClosing() {
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }

    override fun moveTo(newX: Float, newY: Float) {
        layoutParams.x = newX.toInt()
        layoutParams.y = newY.toInt()
        windowManager.updateViewLayout(this, layoutParams)
    }
}