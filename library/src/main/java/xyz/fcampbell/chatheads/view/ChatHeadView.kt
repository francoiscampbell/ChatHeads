package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * Root layout that must be the parent of whatever will be used as a chat head.
 */
class ChatHeadView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(
        context,
        attrs,
        defStyleAttr) {

    companion object {
        const val TAG = "ChatHeadView"
    }

    private val root = LinearLayout(context, attrs, defStyleAttr)
    private val chatHeadIcons = RecyclerView(context, attrs, defStyleAttr)
    private val chatHeadPages = ViewPager(context, attrs)

    private val orchestrator = Orchestrator(context, chatHeadIcons, chatHeadPages, true)

    init {
        removeAllViews() //We don't care about children
    }

    fun initialize(chatHeadAdapter: ChatHeadAdapter) {
        root.orientation = LinearLayout.VERTICAL

        orchestrator.setup(chatHeadAdapter)
        root.addView(chatHeadIcons)
        root.addView(chatHeadPages)

        addView(root)
    }

    fun open() = orchestrator.open()

    fun close() = orchestrator.close()

    class Orchestrator @JvmOverloads constructor(
            context: Context,
            private val icons: RecyclerView,
            private val pages: ViewPager,
            var opened: Boolean = false) {

        private val onChatHeadIconClickedListener = { position: Int ->
            if (opened) {
                if (position == pages.currentItem) { //If we click on the current icon
                    close()
                } else {
                    pages.setCurrentItem(position, true)
                }
            } else {
                open()
            }
        }

        private val onChatHeadPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) = icons.smoothScrollToPosition(position)
        }

        private val collapsingLinearLayoutManager = CollapsingLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        fun setup(chatHeadAdapter: ChatHeadAdapter) {
            val iconAdapter = chatHeadAdapter.iconAdapter
            iconAdapter.chatHeadClickedListener = onChatHeadIconClickedListener
            icons.apply {
                adapter = iconAdapter
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutManager = collapsingLinearLayoutManager
            }

            pages.apply {
                addOnPageChangeListener(onChatHeadPageChangeListener)
                adapter = chatHeadAdapter.pageAdapter
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            }
        }

        fun open() {
            if (opened) return

            animatePagesScale(1f)
            collapsingLinearLayoutManager.expand()

            opened = true
        }

        fun close() {
            if (!opened) return

            animatePagesScale(0f)
            collapsingLinearLayoutManager.collapse()

            opened = false
        }

        fun animatePagesScale(finalScale: Float) {
            pages.pivotX = 0f
            pages.pivotY = 0f
            pages.animate()
                    .scaleX(finalScale)
                    .scaleY(finalScale)
                    .setDuration(300)
                    .setInterpolator(OvershootInterpolator(0.5f))
                    .start()
        }

        fun selectChatHead(position: Int) {
            icons.smoothScrollToPosition(position)
            pages.setCurrentItem(position, true)
        }
    }
}