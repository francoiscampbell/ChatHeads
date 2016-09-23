package xyz.fcampbell.chatheads.view

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import xyz.fcampbell.chatheads.view.helpers.ChatHeadOrchestrator

/**
 * Customization of ChatHeadView that implements the specifics of being in a floating system overlay
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
    private val chatHeadsLayoutParams = DEFAULT_CHAT_HEAD_LAYOUT_PARAMS
    private val trashLayoutParams = WindowManager.LayoutParams().apply {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
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
        windowManager.addView(this, DEFAULT_CHAT_HEAD_LAYOUT_PARAMS)
    }

    override fun attachTrash(trash: View) = windowManager.addView(trash, trashLayoutParams)

    override fun detachTrash(trash: View) = windowManager.removeView(trash)

    override fun savePosition() {
        savedX = chatHeadsLayoutParams.x.toFloat()
        savedY = chatHeadsLayoutParams.y.toFloat()
    }

    override fun setLayoutParamsForState(state: State) {
        when (state) {
            State.CLOSED, State.OPENING -> {
                chatHeadsLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                chatHeadsLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                chatHeadsLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
            }
            else -> {
                chatHeadsLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                chatHeadsLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                chatHeadsLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
            }
        }
        windowManager.updateViewLayout(this, chatHeadsLayoutParams)
    }

    override fun dragTo(newX: Float, newY: Float) {
        chatHeadsLayoutParams.x = newX.toInt()
        chatHeadsLayoutParams.y = newY.toInt()
        windowManager.updateViewLayout(this, chatHeadsLayoutParams)
    }

    override fun animateTo(newX: Float, newY: Float, duration: Long) {
        val pvhX = PropertyValuesHolder.ofInt("x", newX.toInt())
        val pvhY = PropertyValuesHolder.ofInt("y", newY.toInt())
        ObjectAnimator.ofPropertyValuesHolder(LayoutParamsObjectAnimatorWrapper(chatHeadsLayoutParams), pvhX, pvhY).apply {
            this.duration = duration
            interpolator = ChatHeadOrchestrator.ANIMATION_INTERPOLATOR
            start()
        }
    }

    private inner class LayoutParamsObjectAnimatorWrapper(private val layoutParams: WindowManager.LayoutParams) {
        @Suppress("unused") //reflection
        var x: Int
            get() = layoutParams.x
            set(value) {
                layoutParams.x = value
                windowManager.updateViewLayout(this@FloatingChatHeadView, layoutParams)
            }

        @Suppress("unused") //reflection
        var y: Int
            get() = layoutParams.y
            set(value) {
                layoutParams.y = value
                windowManager.updateViewLayout(this@FloatingChatHeadView, layoutParams)
            }
    }

    companion object {
        val DEFAULT_CHAT_HEAD_LAYOUT_PARAMS = WindowManager.LayoutParams().apply {
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
    }
}