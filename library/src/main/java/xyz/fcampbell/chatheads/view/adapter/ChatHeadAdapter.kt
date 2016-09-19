package xyz.fcampbell.chatheads.view.adapter

import android.support.annotation.LayoutRes
import android.view.View
import kotlin.properties.Delegates

/**
 * Adapts a data set into an icon and a page to be shown when the icon is expanded
 */
abstract class ChatHeadAdapter(
        thumbnail: View?,
        @LayoutRes private val iconLayout: Int
) : ChatHeadIconAdapter.Delegate, ChatHeadPagerAdapter.Delegate {
    internal val iconAdapter = ChatHeadIconAdapter(iconLayout, this)
    internal val pageAdapter = ChatHeadPagerAdapter(this)

    var thumbnail: View? by Delegates.observable(thumbnail, { property, oldValue, newValue ->
        onThumbnailChangedListener?.invoke()
    })
    internal var onThumbnailChangedListener: (() -> Unit)? = null

    abstract fun getChatHeadCount(): Int

    override fun getIconCount() = getChatHeadCount()
    abstract override fun bindIcon(icon: View, position: Int)

    override fun getPageCount() = getChatHeadCount()
    abstract override fun getPage(position: Int): View

    open fun onChatHeadSelected(position: Int) {
    }

    open fun onOpening() {
    }

    open fun onOpen() {
    }

    open fun onClosing() {
    }

    open fun onClose() {
    }

    fun notifyDataSetChanged() {
        iconAdapter.notifyDataSetChanged()
        pageAdapter.notifyDataSetChanged()
    }

    //TODO add other dataset changed methods
}