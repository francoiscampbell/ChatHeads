package xyz.fcampbell.chatheads.view.adapter

import android.support.annotation.LayoutRes
import android.view.View
import kotlinx.android.synthetic.main.layout_chat_head_view.view.*
import xyz.fcampbell.chatheads.R
import xyz.fcampbell.chatheads.view.ChatHeadView

/**
 * Adapts a data set into an icon and a page to be shown when the icon is expanded
 */
abstract class ChatHeadAdapter(
        @LayoutRes private val iconLayout: Int,
        @LayoutRes private val pageLayout: Int
) : ChatHeadIconAdapter.Delegate, ChatHeadPagerAdapter.Delegate {
    internal val iconAdapter = ChatHeadIconAdapter(iconLayout, this)
    internal val pageAdapter = ChatHeadPagerAdapter(pageLayout, this)
    internal val onStateChangeListeners = mutableSetOf<(ChatHeadView.State) -> Unit>()

    abstract override fun getItemCount(): Int
    abstract override fun bindIcon(container: View, position: Int)
    abstract override fun bindPage(container: View, position: Int)

    open fun bindThumbnail(container: View) {
        container.defaultThumbnail.setImageResource(R.drawable.ic_default_thumbnail_48dp) //TODO get a proper default thumb
    }

    fun addOnStateChangeListener(listener: (ChatHeadView.State) -> Unit) {
        onStateChangeListeners += listener
    }

    fun removeOnStateChangeListener(listener: (ChatHeadView.State) -> Unit) {
        onStateChangeListeners -= listener
    }

    internal fun onStateChange(newState: ChatHeadView.State) {
        onStateChangeListeners.forEach { it.invoke(newState) }
    }

    open fun onChatHeadSelected(position: Int) {
    }

    fun notifyDataSetChanged() {
        iconAdapter.notifyDataSetChanged()
        pageAdapter.notifyDataSetChanged()
    }

    //TODO add other dataset changed methods
}