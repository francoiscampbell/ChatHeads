package xyz.fcampbell.chatheads.view

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.WindowManager
import xyz.fcampbell.chatheads.view.helpers.ChatHeadOrchestrator

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
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
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

    override fun setLayoutParamsForState(state: State) {
        when (state) {
            State.CLOSED, State.OPENING -> {
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
            }
            else -> {
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
            }
        }
        windowManager.updateViewLayout(this, layoutParams)
    }

    override fun dragTo(newX: Float, newY: Float) {
        layoutParams.x = newX.toInt()
        layoutParams.y = newY.toInt()
        windowManager.updateViewLayout(this, layoutParams)
    }

    override fun animateTo(newX: Float, newY: Float, duration: Long) {
        val pvhX = PropertyValuesHolder.ofInt("x", newX.toInt())
        val pvhY = PropertyValuesHolder.ofInt("y", newY.toInt())
        ObjectAnimator.ofPropertyValuesHolder(LayoutParamsObjectAnimatorWrapper(layoutParams), pvhX, pvhY).apply {
            this.duration = duration
            interpolator = ChatHeadOrchestrator.ANIMATION_INTERPOLATOR
            start()
        }
    }

    private inner class LayoutParamsObjectAnimatorWrapper(private val layoutParams: WindowManager.LayoutParams) {
        var x: Int
            get() = layoutParams.x
            set(value) {
                layoutParams.x = value
                windowManager.updateViewLayout(this@FloatingChatHeadView, layoutParams)
            }

        var y: Int
            get() = layoutParams.y
            set(value) {
                layoutParams.y = value
                windowManager.updateViewLayout(this@FloatingChatHeadView, layoutParams)
            }
    }
}