package xyz.fcampbell.chatheads.view

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.widget.FrameLayout
import xyz.fcampbell.chatheads.R
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter
import xyz.fcampbell.chatheads.view.helpers.ChatHeadOrchestrator

/**
 * Root layout that must be the parent of whatever will be used as a chat head.
 */
class ChatHeadView internal constructor(
        context: Context
) : FrameLayout(context, null, 0), ChatHeadOrchestrator.Orchestrable {
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val chatHeadsRoot = layoutInflater.inflate(R.layout.layout_chat_head_view, null)
    private val trashRoot = layoutInflater.inflate(R.layout.layout_trash, null)

    private val orchestrator = ChatHeadOrchestrator(this, chatHeadsRoot, trashRoot)

    private lateinit var onTrashListener: () -> Unit

    private val chatHeadsLayoutParams = WindowManager.LayoutParams().apply {
        copyFrom(DEFAULT_CHAT_HEAD_LAYOUT_PARAMS)
    }
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

    fun initialize(adapter: ChatHeadAdapter, onTrashListener: () -> Unit) {
        this.onTrashListener = onTrashListener

        removeAllViews() //Remove any children set in XML
        addView(chatHeadsRoot)

        orchestrator.setup(adapter)
    }

    fun attachToWindow() {
        windowManager.addView(this, DEFAULT_CHAT_HEAD_LAYOUT_PARAMS)
    }

    fun detachFromWindow() {
        windowManager.removeView(this)
    }

    fun open() = orchestrator.open()

    fun close() = orchestrator.close()

    //location of touch event relative to view's top-left corner
    private var dragPointerOffsetX: Float = 0f
    private var dragPointerOffsetY: Float = 0f
    private var dragging = false
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val screenPos = IntArray(2)
                getLocationOnScreen(screenPos)
                dragPointerOffsetX = event.rawX - screenPos[0]
                dragPointerOffsetY = event.rawY - screenPos[1]
            }
            MotionEvent.ACTION_MOVE -> {
                if (orchestrator.state == State.CLOSED) {
                    dragging = true

                    orchestrator.showTrash()
                    dragTo(event.rawX - dragPointerOffsetX, event.rawY - dragPointerOffsetY)
                }
            }
            else -> {
                if (dragging) {
                    dragging = false

                    orchestrator.hideTrash()
                    if (orchestrator.checkTrashIntersect()) {
                        onTrashListener()
                    }
                    return false
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


    override fun attachTrash(trash: View) = windowManager.addView(trash, trashLayoutParams)

    override fun detachTrash(trash: View) = windowManager.removeView(trash)

    private var savedX = 0f
    private var savedY = 0f
    override fun savePosition() {
        savedX = chatHeadsLayoutParams.x.toFloat()
        savedY = chatHeadsLayoutParams.y.toFloat()
    }

    override fun restorePosition() = animateTo(savedX, savedY, ChatHeadOrchestrator.THUMBNAIL_MOVE_ANIMATION_DURATION)

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