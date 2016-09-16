package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout

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

    private val root = LinearLayout(context, attrs, defStyleAttr)
    private val chatHeadStrip = RecyclerView(context, attrs, defStyleAttr)
    private val chatHeadPages = ViewPager(context, attrs)

    init {
        removeAllViews() //We don't care about children
    }

    fun initialize(chatHeadAdapter: ChatHeadAdapter) {
        root.orientation = LinearLayout.VERTICAL

        chatHeadStrip.apply {
            adapter = chatHeadAdapter.iconAdapter
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        root.addView(chatHeadStrip)

        chatHeadPages.apply {
            adapter = chatHeadAdapter.pageAdapter
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        root.addView(chatHeadPages)

        addView(root)
    }
}