package xyz.fcampbell.chatheads.view

import android.graphics.drawable.Drawable
import android.view.View

/**
 * Adapts a data set into an icon and a page to be shown when the icon is expanded
 */
abstract class ChatHeadAdapter() : ChatHeadIconAdapter.Delegate, ChatHeadPagerAdapter.Delegate {
    val iconAdapter = ChatHeadIconAdapter(this)
    val pageAdapter = ChatHeadPagerAdapter(this)

    abstract fun getChatHeadCount(): Int

    override fun getIconCount() = getChatHeadCount()
    abstract override fun getIcon(position: Int): Drawable

    override fun getPageCount() = getChatHeadCount()
    abstract override fun getPage(position: Int): View

    fun notifyDataSetChanged() {
        iconAdapter.notifyDataSetChanged()
        pageAdapter.notifyDataSetChanged()
    }

    //TODO add other dataset changed methods
}