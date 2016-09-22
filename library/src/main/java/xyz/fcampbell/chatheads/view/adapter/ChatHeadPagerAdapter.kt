package xyz.fcampbell.chatheads.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.henrytao.recyclerpageradapter.RecyclerPagerAdapter

/**
 * Adapts a ChatHead's page property into a ViewPager
 */
internal class ChatHeadPagerAdapter(
        @LayoutRes val pageLayout: Int,
        val delegate: Delegate
) : RecyclerPagerAdapter<ChatHeadPagerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val pageView = layoutInflater.inflate(pageLayout, parent, false)
        return ViewHolder(pageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        delegate.bindPage(holder.itemView, position)
    }

    override fun getItemCount() = delegate.getItemCount()

    internal class ViewHolder(itemView: View) : RecyclerPagerAdapter.ViewHolder(itemView)

    interface Delegate {
        fun getItemCount(): Int
        fun bindPage(page: View, position: Int)
    }
}