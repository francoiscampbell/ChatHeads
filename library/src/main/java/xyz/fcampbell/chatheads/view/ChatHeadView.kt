package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
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

    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val root = layoutInflater.inflate(R.layout.layout_chat_head_view, null)
    private val thumbnailContainer = root.findViewById(R.id.thumbnailContainer) as FrameLayout
    private val icons = root.findViewById(R.id.icons) as RecyclerView
    private val pages = root.findViewById(R.id.pages) as ViewPager

    internal val orchestrator = ChatHeadOrchestrator(thumbnailContainer, icons, pages)

    init {
        removeAllViews() //Remove any children set in XML
        addView(root)
    }

    open fun initialize(adapter: ChatHeadAdapter) {
        orchestrator.setup(adapter)
    }

    fun open() = orchestrator.open()

    fun close() = orchestrator.close()

    var dX: Float = 0f
    var dY: Float = 0f
    var dragging = false
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = x - event.rawX
                dY = y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (orchestrator.state == State.CLOSED) {
                    dragging = true
                    moveTo(event.rawX + dX, event.rawY + dY)
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

    open fun moveTo(newX: Float, newY: Float) = animate().x(newX).y(newY).setDuration(0).start()
}