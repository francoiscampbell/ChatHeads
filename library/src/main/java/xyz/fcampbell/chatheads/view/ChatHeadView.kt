package xyz.fcampbell.chatheads.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter
import xyz.fcampbell.chatheads.view.helpers.ChatHeadOrchestrator

/**
 * Root layout that must be the parent of whatever will be used as a chat head.
 */
open class ChatHeadView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(
        context,
        attrs,
        defStyleAttr), ChatHeadOrchestrator.Orchestrable {
    enum class State {
        CLOSED, OPENING, OPEN, CLOSING
    }

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val root = layoutInflater.inflate(R.layout.layout_chat_head_view, null)
    private val thumbnailContainer = root.thumbnailContainer
    private val contentContainer = root.contentContainer

    private val orchestrator = ChatHeadOrchestrator(this, thumbnailContainer, contentContainer)

    init {
        removeAllViews() //Remove any children set in XML
        addView(root)
    }

    fun initialize(adapter: ChatHeadAdapter) {
        orchestrator.setup(adapter)
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
                    dragTo(event.rawX - dragPointerOffsetX, event.rawY - dragPointerOffsetY)
                }
            }
            else -> {
                if (dragging) {
                    dragging = false
                    return false
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    protected var oldX = 0f
    protected var oldY = 0f
    override fun savePosition() {
        oldX = x
        oldY = y
    }

    override fun setLayoutParamsForState(state: State) {
        when (state) {
            ChatHeadView.State.CLOSED -> {
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            else -> {
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun restorePosition() {
        animateTo(oldX, oldY, ChatHeadOrchestrator.ANIMATION_DURATION)
    }

    override fun dragTo(newX: Float, newY: Float) = animateTo(newX, newY, 0)

    override fun animateTo(newX: Float, newY: Float, duration: Long) {
        animate().x(newX).y(newY).setDuration(duration).start()
    }
}