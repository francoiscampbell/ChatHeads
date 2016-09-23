package xyz.fcampbell.chatheads.view.helpers

import android.animation.TimeInterpolator
import android.graphics.Rect
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.layout_chat_head_view.view.*
import xyz.fcampbell.chatheads.view.ChatHeadView
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter
import kotlin.properties.Delegates

/**
 * Orchestrates between the RecyclerView and the ViewPager
 */
internal class ChatHeadOrchestrator @JvmOverloads constructor(
        private val orchestrable: Orchestrable,
        chatHeadRoot: View,
        private val trashRoot: View,
        initialState: ChatHeadView.State = ChatHeadView.State.CLOSED) {
    private lateinit var adapter: ChatHeadAdapter

    internal var state by Delegates.observable(initialState, { kProperty, oldState, newState ->
        orchestrable.setLayoutParamsForState(newState)
    })

    private val onThumbnailClickListener = { thumbnail: View ->
        when (state) {
            ChatHeadView.State.OPEN, ChatHeadView.State.OPENING -> close()
            ChatHeadView.State.CLOSED, ChatHeadView.State.CLOSING, ChatHeadView.State.IN_POSITION -> open()
        }
    }

    private val onIconClickListener = { position: Int ->
        if (state == ChatHeadView.State.OPEN) {
            if (position == pages.currentItem) {
                close()
            } else {
                selectChatHead(position)
            }
        }
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageSelected(position: Int) = selectChatHead(position)
    }

    private val contentContainer = chatHeadRoot.contentContainer
    private val icons = contentContainer.icons
    private val pages = contentContainer.pages

    private val thumbnailContainer = chatHeadRoot.thumbnailContainer

    fun setup(chatHeadAdapter: ChatHeadAdapter) {
        adapter = chatHeadAdapter

        adapter.bindThumbnail(thumbnailContainer)
        thumbnailContainer.apply {
            setOnClickListener(onThumbnailClickListener)
            visibility = if (state == ChatHeadView.State.OPEN) View.GONE else View.VISIBLE
        }

        contentContainer.visibility = if (state == ChatHeadView.State.CLOSED) View.GONE else View.VISIBLE

        chatHeadAdapter.iconAdapter.chatHeadClickedListener = onIconClickListener
        icons.apply {
            adapter = chatHeadAdapter.iconAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            //offset the child drawing by 1 and return the first child on the last drawing iteration to keep it in front
            setChildDrawingOrderCallback { childCount, iteration -> (iteration + 1) % childCount }
        }

        pages.apply {
            addOnPageChangeListener(onPageChangeListener)
            adapter = chatHeadAdapter.pageAdapter
        }
    }

    fun open() {
        if (state == ChatHeadView.State.OPEN || state == ChatHeadView.State.OPENING) return

        orchestrable.savePosition()
        orchestrable.animateTo(0f, 0f, THUMBNAIL_MOVE_ANIMATION_DURATION)

        state = ChatHeadView.State.OPENING

        setPagesPivotToTop()
        pages.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(OPEN_CLOSE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withStartAction {
                    state = ChatHeadView.State.IN_POSITION
                    contentContainer.visibility = View.VISIBLE
                }
                .withEndAction({
                    state = ChatHeadView.State.OPEN
                })
                .setStartDelay(THUMBNAIL_MOVE_ANIMATION_DURATION)
                .start()

        thumbnailContainer.animate()
                .alpha(0f)
                .setDuration(OPEN_CLOSE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withEndAction {
                    thumbnailContainer.visibility = View.GONE
                }
                .setStartDelay(THUMBNAIL_MOVE_ANIMATION_DURATION)
                .start()

        icons.forEachChild { child ->
            child.animate()
                    .translationX(0f)
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(OPEN_CLOSE_ANIMATION_DURATION)
                    .setInterpolator(ANIMATION_INTERPOLATOR)
                    .withEndAction {
                        icons.requestLayout()
                    }
                    .setStartDelay(THUMBNAIL_MOVE_ANIMATION_DURATION)
                    .start()
        }
    }

    fun close() {
        if (state == ChatHeadView.State.CLOSED || state == ChatHeadView.State.CLOSING) return

        state = ChatHeadView.State.CLOSING

        setPagesPivotToTop()
        pages.animate()
                .scaleX(PAGES_CLOSE_SCALE)
                .scaleY(PAGES_CLOSE_SCALE)
                .alpha(0f)
                .setDuration(OPEN_CLOSE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withEndAction({
                    state = ChatHeadView.State.IN_POSITION
                    contentContainer.visibility = View.GONE
                    orchestrable.restorePosition()
                    state = ChatHeadView.State.CLOSED
                })
                .start()

        adapter.bindThumbnail(thumbnailContainer)
        thumbnailContainer.animate()
                .alpha(1f)
                .setDuration(OPEN_CLOSE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withStartAction {
                    thumbnailContainer.visibility = View.VISIBLE
                }
                .start()

        icons.forEachChild { child ->
            child.animate()
                    .translationX(-child.x)
                    .translationY(-child.y)
                    .alpha(0f)
                    .setDuration(OPEN_CLOSE_ANIMATION_DURATION)
                    .setInterpolator(ANIMATION_INTERPOLATOR)
                    .start()
        }
    }

    private var trashShown = false
    fun showTrash() {
        if (trashShown) return

        trashShown = true
        adapter.bindTrash(trashRoot)
        trashRoot.animate()
                .alpha(1f)
                .setDuration(TRASH_FADE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withStartAction {
                    trashRoot.visibility = View.VISIBLE
                    orchestrable.attachTrash(trashRoot)
                }
                .start()
    }

    fun hideTrash() {
        if (!trashShown) return

        trashRoot.animate()
                .alpha(0f)
                .setDuration(TRASH_FADE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withEndAction {
                    orchestrable.detachTrash(trashRoot)
                    trashRoot.visibility = View.GONE
                    trashShown = false
                }
                .start()
    }

    fun emphasizeTrash() {
        trashRoot.animate()
                .scaleX(TRASH_EMPHASIS_SCALE)
                .scaleY(TRASH_EMPHASIS_SCALE)
                .setDuration(BASE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .start()
    }

    fun deEmphasizeTrash() {
        trashRoot.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(BASE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .start()
    }

    private val thumbScreenLocation = IntArray(2)
    private val trashScreenLocation = IntArray(2)
    private val thumbRect = Rect()
    private val trashRect = Rect()
    fun checkTrashIntersect(): Boolean {
        thumbnailContainer.getLocationOnScreen(thumbScreenLocation)
        thumbnailContainer.getGlobalVisibleRect(thumbRect)
        thumbRect.offset(thumbScreenLocation[0], thumbScreenLocation[1])

        trashRoot.getLocationOnScreen(trashScreenLocation)
        trashRoot.getGlobalVisibleRect(trashRect)
        trashRect.offset(trashScreenLocation[0], trashScreenLocation[1])

        return Rect.intersects(thumbRect, trashRect)
    }

    private fun setPagesPivotToTop() {
        pages.pivotX = (pages.width / 2).toFloat()
        pages.pivotY = (-icons.height).toFloat()
    }

    fun selectChatHead(position: Int) {
        icons.smoothScrollToPosition(position)
        pages.setCurrentItem(position, true)

        adapter.onChatHeadSelected(position)
    }

    inline fun ViewGroup.forEachChild(action: (View) -> Unit) {
        for (i in 0..childCount - 1) action(getChildAt(i))
    }

    internal interface Orchestrable {

        fun setLayoutParamsForState(state: ChatHeadView.State)
        fun savePosition()

        fun restorePosition()
        fun dragTo(newX: Float, newY: Float)

        fun animateTo(newX: Float, newY: Float, duration: Long)
        fun attachTrash(trash: View)
        fun detachTrash(trash: View)

    }

    companion object {
        const val BASE_ANIMATION_DURATION = 100L
        const val OPEN_CLOSE_ANIMATION_DURATION = BASE_ANIMATION_DURATION
        const val THUMBNAIL_MOVE_ANIMATION_DURATION = 2 * BASE_ANIMATION_DURATION
        const val TRASH_FADE_ANIMATION_DURATION = 5 * BASE_ANIMATION_DURATION
        val ANIMATION_INTERPOLATOR: TimeInterpolator
            get() = OvershootInterpolator(0.5f)//new instance every time to run simultaneous animations

        const val PAGES_CLOSE_SCALE = 0.9f
        const val TRASH_EMPHASIS_SCALE = 1.2f
    }
}