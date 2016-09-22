package xyz.fcampbell.chatheads.view

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.WindowManager
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * Created by francois on 2016-09-22.
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
        flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        format = PixelFormat.TRANSLUCENT
    }

    private var oldX = 0f
    private var oldY = 0f
    private val onStateChange: (State) -> Unit = { newState ->
        when (newState) {
            State.OPENING -> {
                oldX = layoutParams.x.toFloat()
                oldY = layoutParams.y.toFloat()

                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                moveTo(0f, 0f)
            }
            State.CLOSING -> {
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                moveTo(oldX, oldY)
            }
            else -> Unit
        }
    }

    override fun initialize(adapter: ChatHeadAdapter) {
        super.initialize(adapter)

        adapter.addOnStateChangeListener(onStateChange)
    }

    fun attachToWindow(windowManager: WindowManager) {
        this.windowManager = windowManager
        windowManager.addView(this, layoutParams)
    }

    override fun moveTo(newX: Float, newY: Float) {
        layoutParams.x = newX.toInt()
        layoutParams.y = newY.toInt()
        windowManager.updateViewLayout(this, layoutParams)
    }
}