package xyz.fcampbell.chatheads.view

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.PixelFormat
import android.os.Vibrator
import android.view.*
import android.widget.FrameLayout
import xyz.fcampbell.chatheads.R
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter
import xyz.fcampbell.chatheads.view.helpers.ChatHeadOrchestrator

/**
 * Root layout that must be the parent of whatever will be used as a chat head.
 */
class ChatHeadView internal constructor(
        context: Context,
        adapter: ChatHeadAdapter
) : FrameLayout(context, null, 0), ChatHeadOrchestrator.Orchestrable {
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val chatHeadsRoot = layoutInflater.inflate(R.layout.layout_chat_head_view, null)
    private val trashRoot = layoutInflater.inflate(R.layout.layout_trash, null)

    private val orchestrator = ChatHeadOrchestrator(this, chatHeadsRoot, trashRoot)

    var onTrashListener: (() -> Unit)? = null
    private val onTrashIntersectListener = { vibrator.vibrate(adapter.getTrashVibrateMillis()) }

    private var attachedToWindow = false

    private val currentChatHeadsLayoutParams = WindowManager.LayoutParams().apply {
        copyFrom(DEFAULT_CHAT_HEAD_LAYOUT_PARAMS)
    }

    init {
        removeAllViews() //Remove any children set in XML
        addView(chatHeadsRoot)

        orchestrator.setup(adapter)
    }

    fun attachToWindow() {
        checkAttached(false)
        windowManager.addView(this, DEFAULT_CHAT_HEAD_LAYOUT_PARAMS)
        attachedToWindow = true
    }

    fun detachFromWindow() {
        checkAttached(true)
        windowManager.removeView(this)
        attachedToWindow = false
    }

    fun open() {
        checkAttached(true)
        orchestrator.open()
    }

    fun close() {
        checkAttached(true)
        orchestrator.close()
    }

    private fun checkAttached(shouldBeAttached: Boolean) {
        if (shouldBeAttached && !attachedToWindow) {
            throw IllegalStateException("The ChatHeadView should be attached to its window but is not")
        }
        if (!shouldBeAttached && attachedToWindow) {
            throw IllegalStateException("The ChatHeadView should not be attached to its window but is")
        }
    }

    //location of touch event relative to view's top-left corner
    private val screenPos = IntArray(2)
    private var dragPointerOffsetX: Float = 0f
    private var dragPointerOffsetY: Float = 0f
    private var dragging = false
    private var inTrashIntersect = false
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                getLocationOnScreen(screenPos)
                dragPointerOffsetX = event.rawX - screenPos[0]
                dragPointerOffsetY = event.rawY - screenPos[1]
            }
            MotionEvent.ACTION_MOVE -> {
                if (orchestrator.state == State.CLOSED) {
                    dragging = true

                    orchestrator.showTrash()
                    dragTo(event.rawX - dragPointerOffsetX, event.rawY - dragPointerOffsetY)
                    if (orchestrator.checkTrashIntersect()) {
                        if (!inTrashIntersect) {
                            inTrashIntersect = true
                            onTrashIntersectListener.invoke()
                            orchestrator.emphasizeTrash()
                        }
                    } else {
                        inTrashIntersect = false
                        orchestrator.deEmphasizeTrash()
                    }
                }
            }
            else -> {
                if (dragging) {
                    dragging = false

                    orchestrator.hideTrash()
                    if (orchestrator.checkTrashIntersect()) {
                        onTrashListener?.invoke()
                    }
                    return false
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun showTrash(trash: View) = windowManager.addView(trash, TRASH_LAYOUT_PARAMS)

    override fun hideTrash(trash: View) = windowManager.removeView(trash)

    private var savedX = 0f
    private var savedY = 0f
    override fun savePosition() {
        savedX = currentChatHeadsLayoutParams.x.toFloat()
        savedY = currentChatHeadsLayoutParams.y.toFloat()
    }

    override fun restorePosition() = animateTo(savedX, savedY, ChatHeadOrchestrator.THUMBNAIL_MOVE_ANIMATION_DURATION)

    override fun setLayoutParamsForState(state: State) {
        when (state) {
            State.CLOSED, State.OPENING -> {
                currentChatHeadsLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                currentChatHeadsLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                currentChatHeadsLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
            }
            else -> {
                currentChatHeadsLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                currentChatHeadsLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                currentChatHeadsLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
            }
        }
        windowManager.updateViewLayout(this, currentChatHeadsLayoutParams)
    }

    override fun dragTo(newX: Float, newY: Float) {
        currentChatHeadsLayoutParams.x = newX.toInt()
        currentChatHeadsLayoutParams.y = newY.toInt()
        windowManager.updateViewLayout(this, currentChatHeadsLayoutParams)
    }

    override fun animateTo(newX: Float, newY: Float, duration: Long) {
        val pvhX = PropertyValuesHolder.ofInt("x", newX.toInt())
        val pvhY = PropertyValuesHolder.ofInt("y", newY.toInt())
        ObjectAnimator.ofPropertyValuesHolder(LayoutParamsObjectAnimatorWrapper(currentChatHeadsLayoutParams), pvhX, pvhY).apply {
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
                windowManager.updateViewLayout(this@ChatHeadView, layoutParams)
            }

        @Suppress("unused") //reflection
        var y: Int
            get() = layoutParams.y
            set(value) {
                layoutParams.y = value
                windowManager.updateViewLayout(this@ChatHeadView, layoutParams)
            }
    }

    companion object {
        private val DEFAULT_CHAT_HEAD_LAYOUT_PARAMS = WindowManager.LayoutParams().apply {
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
        private val TRASH_LAYOUT_PARAMS = WindowManager.LayoutParams().apply {
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
    }

    enum class State {
        /**
         * Thumbnail visible and floating
         */
        CLOSED,

        /**
         * Thumbnail moving towards (0,0)
         */
        OPENING,

        /**
         * Thumbnail has moved and content is ready to open
         */
        IN_POSITION,

        /**
         * Content is open
         */
        OPEN,

        /**
         * Thumbnail moving back towards its original location
         */
        CLOSING
    }

}