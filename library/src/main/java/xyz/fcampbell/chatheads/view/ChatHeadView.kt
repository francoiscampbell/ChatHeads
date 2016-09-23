package xyz.fcampbell.chatheads.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import xyz.fcampbell.chatheads.R
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
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val chatHeadsRoot = layoutInflater.inflate(R.layout.layout_chat_head_view, null)
    private val trashRoot = layoutInflater.inflate(R.layout.layout_trash, null)

    private val orchestrator by lazy { ChatHeadOrchestrator(this, chatHeadsRoot, trashRoot) }

    private lateinit var onTrashListener: () -> Unit

    fun initialize(adapter: ChatHeadAdapter, trashListener: () -> Unit) {
        this.onTrashListener = trashListener

        removeAllViews() //Remove any children set in XML
        addView(chatHeadsRoot)

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

    /**
     * Attach the trash view to show it. Guaranteed to only be called if the trash is not already attached
     */
    override fun attachTrash(trash: View) = addView(trash)

    /**
     * Detach the trash view to hide it. Guaranteed to only be called if the trash is already attached
     */
    override fun detachTrash(trash: View) = removeView(trash)


    protected var savedX = 0f
    protected var savedY = 0f
    override fun savePosition() {
        savedX = x
        savedY = y
    }

    override fun restorePosition() {
        animateTo(savedX, savedY, ChatHeadOrchestrator.ANIMATION_DURATION)
    }

    override fun setLayoutParamsForState(state: State) {
        when (state) {
            State.CLOSED -> {
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            else -> {
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun dragTo(newX: Float, newY: Float) = animateTo(newX, newY, 0)

    override fun animateTo(newX: Float, newY: Float, duration: Long) {
        animate().x(newX).y(newY).setDuration(duration).start()
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