package xyz.fcampbell.chatheads.view

import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.OvershootInterpolator
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter
import kotlin.properties.Delegates

/**
 * Orchestrates between the RecyclerView and the ViewPager
 */
internal class ChatHeadOrchestrator @JvmOverloads constructor(
        private val thumbnail: View,
        private val icons: CollapsingRecyclerView,
        private val pages: ViewPager,
        initialState: State = ChatHeadOrchestrator.State.CLOSED) {
    private lateinit var adapter: ChatHeadAdapter

    enum class State { //Nested class in ChatHeadView to be in public API
        CLOSED, OPENING, OPEN, CLOSING
    }

    private var state: State by Delegates.observable(initialState, { property, oldState, newState ->
        when (newState) {
            ChatHeadOrchestrator.State.CLOSED -> adapter.onClose()
            ChatHeadOrchestrator.State.OPENING -> adapter.onOpening()
            ChatHeadOrchestrator.State.OPEN -> adapter.onOpen()
            ChatHeadOrchestrator.State.CLOSING -> adapter.onClosing()
        }
    })

    private val onThumbnailClickListener = { thumbnail: View ->
        when (state) {
            State.OPEN, State.OPENING -> close()
            State.CLOSED, State.CLOSING -> open()
        }
    }

    private val onIconClickListener = { position: Int ->
        if (state == State.OPEN) {
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

    fun setup(chatHeadAdapter: ChatHeadAdapter) {
        adapter = chatHeadAdapter

        thumbnail.apply {
            setOnClickListener(onThumbnailClickListener)
            visibility = when (state) {
                ChatHeadOrchestrator.State.OPEN -> View.GONE
                else -> View.VISIBLE
            }
        }

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
        if (state == State.OPEN || state == State.OPENING) return

        state = State.OPENING
        setPagesPivotToTop()
        pages.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(OPEN_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withStartAction { pages.visibility = View.VISIBLE }
                .withEndAction({ state = State.OPEN })
                .start()

        thumbnail.animate()
                .alpha(0f)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withEndAction({ thumbnail.visibility = View.GONE })
                .start()


        icons.expandWithAnimation()
    }

    fun close() {
        if (state == State.CLOSED || state == State.CLOSING) return

        state = State.CLOSING
        setPagesPivotToTop()
        pages.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .alpha(0f)
                .setDuration(CLOSE_ANIMATION_DURATION)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withEndAction({
                    state = State.CLOSED
                    pages.visibility = View.GONE
                })
                .start()

        thumbnail.animate()
                .alpha(1f)
                .setInterpolator(ANIMATION_INTERPOLATOR)
                .withStartAction({ thumbnail.visibility = View.VISIBLE })
                .start()

        icons.collapseWithAnimation()
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

    companion object {
        const val OPEN_ANIMATION_DURATION = 100L
        const val CLOSE_ANIMATION_DURATION = 100L

        val ANIMATION_INTERPOLATOR = OvershootInterpolator(0.5f)
    }
}