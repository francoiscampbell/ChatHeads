package xyz.fcampbell.chatheads.view.helpers

import android.animation.TimeInterpolator
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.layout_chat_head_view.view.*
import xyz.fcampbell.chatheads.view.ChatHeadView
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * Orchestrates between the RecyclerView and the ViewPager
 */
internal class ChatHeadOrchestrator @JvmOverloads constructor(
        private val orchestrable: Orchestrable,
        private val thumbnailContainer: ViewGroup,
        private val contentContainer: ViewGroup,
        internal var state: ChatHeadView.State = ChatHeadView.State.CLOSED) {
    private lateinit var adapter: ChatHeadAdapter

    private val onThumbnailClickListener = { thumbnail: View ->
        when (state) {
            ChatHeadView.State.OPEN, ChatHeadView.State.OPENING -> close()
            ChatHeadView.State.CLOSED, ChatHeadView.State.CLOSING -> open()
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

    private val icons = contentContainer.icons
    private val pages = contentContainer.pages

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

        state = ChatHeadView.State.OPENING

        orchestrable.savePosition()
        orchestrable.animateTo(0f, 0f, ANIMATION_DURATION)

        setPagesPivotToTop()
        pages.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withStartAction {
                    contentContainer.visibility = View.VISIBLE
                }
                .withEndAction({
                    state = ChatHeadView.State.OPEN
                    orchestrable.setLayoutParamsForState(state)
                })
                .setStartDelay(ANIMATION_DURATION)
                .start()

        thumbnailContainer.animate()
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withEndAction {
                    thumbnailContainer.visibility = View.GONE
                }
                .setStartDelay(ANIMATION_DURATION)
                .start()

        icons.forEachChild { child ->
            child.animate()
                    .translationX(0f)
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(ANIMATION_DURATION)
                    .setInterpolator(ANIMATION_INTERPOLATOR)
                    .setStartDelay(ANIMATION_DURATION)
                    .start()
        }
    }

    fun close() {
        if (state == ChatHeadView.State.CLOSED || state == ChatHeadView.State.CLOSING) return

        state = ChatHeadView.State.CLOSING

        setPagesPivotToTop()
        pages.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withEndAction({
                    state = ChatHeadView.State.CLOSED
                    contentContainer.visibility = View.GONE
                    orchestrable.setLayoutParamsForState(state)
                    orchestrable.restorePosition()
                })
                .start()

        adapter.bindThumbnail(thumbnailContainer)
        thumbnailContainer.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
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
                    .setDuration(ANIMATION_DURATION)
                    .setInterpolator(ANIMATION_INTERPOLATOR)
                    .start()
        }
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

    interface Orchestrable {
        fun setLayoutParamsForState(state: ChatHeadView.State)

        fun savePosition()
        fun restorePosition()

        fun dragTo(newX: Float, newY: Float)
        fun animateTo(newX: Float, newY: Float, duration: Long)
    }

    companion object {
        const val ANIMATION_DURATION = 100L

        val ANIMATION_INTERPOLATOR: TimeInterpolator
            get() = OvershootInterpolator(0.5f)//new instance every time to run simultaneous animations
    }
}