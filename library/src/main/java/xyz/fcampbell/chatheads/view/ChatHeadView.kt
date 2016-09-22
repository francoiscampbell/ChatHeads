package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import xyz.fcampbell.chatheads.R
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

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
        defStyleAttr) {
    enum class State {
        CLOSED, OPENING, OPEN, CLOSING
    }

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val root = layoutInflater.inflate(R.layout.layout_chat_head_view, null)
    private val thumbnailContainer = root.findViewById(R.id.thumbnailContainer) as FrameLayout
    private val icons = root.findViewById(R.id.icons) as RecyclerView
    private val pages = root.findViewById(R.id.pages) as ViewPager

    private val orchestrator = ChatHeadOrchestrator(thumbnailContainer, icons, pages)

    protected var oldX = 0f
    protected var oldY = 0f
    private val onStateChange: (State) -> Unit = { newState ->
        when (newState) {
            State.OPENING -> {
                savePosition()
                setLayoutParamsForOpening()
                moveTo(0f, 0f)
            }
            State.CLOSING -> {
                setLayoutParamsForClosing()
                moveTo(oldX, oldY)
            }
            else -> Unit
        }
    }

    private val initialScreenPos = IntArray(2)
    private var gotInitialScreenPos = false

    init {
        removeAllViews() //Remove any children set in XML
        addView(root)
    }

    fun initialize(adapter: ChatHeadAdapter) {
        orchestrator.setup(adapter)
        adapter.addOnStateChangeListener(onStateChange)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!gotInitialScreenPos) {
            getLocationOnScreen(initialScreenPos)
            gotInitialScreenPos = true
        }
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
                    moveTo(event.rawX - dragPointerOffsetX - initialScreenPos[0], event.rawY - dragPointerOffsetY - initialScreenPos[1])
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (dragging) {
                    dragging = false
                    return false
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    protected open fun savePosition() {
        oldX = x
        oldY = y
    }

    protected open fun setLayoutParamsForOpening() {
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    }

    protected open fun setLayoutParamsForClosing() {
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
    }

    open fun moveTo(newX: Float, newY: Float) = animate().x(newX).y(newY).setDuration(0).start()
}