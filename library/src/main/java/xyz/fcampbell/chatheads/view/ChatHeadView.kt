package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import org.jetbrains.anko.layoutInflater
import xyz.fcampbell.chatheads.R
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

    private val root = context.layoutInflater.inflate(R.layout.layout_chat_head_view, null)
    private val thumbnail = root.findViewById(R.id.thumbnail) as FrameLayout
    private val defaultThumbnail = root.findViewById(R.id.defaultThumbnail) as ImageView
    private val icons = root.findViewById(R.id.icons) as CollapsingRecyclerView
    private val pages = root.findViewById(R.id.pages) as ViewPager

    private val orchestrator = ChatHeadOrchestrator(thumbnail, icons, pages)

    init {
        removeAllViews() //Remove any children set in XML
        addView(root)
    }

    fun initialize(adapter: ChatHeadAdapter) {
        setThumbnail(adapter.thumbnail)
        adapter.onThumbnailChangedListener = { setThumbnail(adapter.thumbnail) }
        orchestrator.setup(adapter)
    }

    private fun setThumbnail(thumbnail: View?) {
        this.thumbnail.removeAllViews()
        this.thumbnail.addView(thumbnail ?: defaultThumbnail)
    }

    fun open() = orchestrator.open()

    fun close() = orchestrator.close()
}