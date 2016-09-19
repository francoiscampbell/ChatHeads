package xyz.fcampbell.chatheads.view

import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
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
        if (state == State.OPEN) selectChatHead(position)
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

        thumbnail.setOnClickListener(onThumbnailClickListener)

        chatHeadAdapter.iconAdapter.chatHeadClickedListener = onIconClickListener
        icons.apply {
            adapter = chatHeadAdapter.iconAdapter
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        pages.apply {
            addOnPageChangeListener(onPageChangeListener)
            adapter = chatHeadAdapter.pageAdapter
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
        }
    }


    fun open() {
        if (state == State.OPEN || state == State.OPENING) return

        state = State.OPENING
        animatePagesScale(1f, { state = State.OPEN })
        icons.expand()
    }

    fun close() {
        if (state == State.CLOSED || state == State.CLOSING) return

        state = State.CLOSING
        animatePagesScale(0f, { state = State.CLOSED })
        icons.collapse()
    }

    fun animatePagesScale(finalScale: Float, endAction: () -> Unit) {
        pages.pivotX = 0f
        pages.pivotY = 0f
        pages.animate()
                .scaleX(finalScale)
                .scaleY(finalScale)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator(0.5f))
                .withEndAction(endAction)
                .start()
    }

    fun selectChatHead(position: Int) {
        icons.smoothScrollToPosition(position)
        pages.setCurrentItem(position, true)

        adapter.onChatHeadSelected(position)
    }
}