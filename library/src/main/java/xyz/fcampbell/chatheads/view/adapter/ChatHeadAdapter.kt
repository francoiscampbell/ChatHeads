package xyz.fcampbell.chatheads.view.adapter

import android.support.annotation.LayoutRes
import android.util.Log
import android.view.View
import android.widget.ImageView
import xyz.fcampbell.chatheads.R

/**
 * Adapts a data set into an icon and a page to be shown when the icon is expanded
 */
abstract class ChatHeadAdapter(
        @LayoutRes private val iconLayout: Int,
        @LayoutRes private val pageLayout: Int
) : ChatHeadIconAdapter.Delegate, ChatHeadPagerAdapter.Delegate {
    internal val iconAdapter = ChatHeadIconAdapter(iconLayout, this)
    internal val pageAdapter = ChatHeadPagerAdapter(pageLayout, this)

    abstract override fun getItemCount(): Int
    abstract override fun bindIcon(icon: View, position: Int)
    abstract override fun bindPage(page: View, position: Int)

    open fun bindThumbnail(view: View) {
        val imageView = view.findViewById(R.id.defaultThumbnail) as ImageView
        imageView.setImageResource(R.drawable.ic_arrow_back_black_48dp) //TODO get a proper default thumb
    }

    open fun onChatHeadSelected(position: Int) {
    }

    open fun onOpening() {
        Log.i("Adapter", "Opening")
    }

    open fun onOpen() {
        Log.i("Adapter", "Open")
    }

    open fun onClosing() {
        Log.i("Adapter", "Closing")
    }

    open fun onClose() {
        Log.i("Adapter", "Close")
    }

    fun notifyDataSetChanged() {
        iconAdapter.notifyDataSetChanged()
        pageAdapter.notifyDataSetChanged()
    }

    //TODO add other dataset changed methods
}