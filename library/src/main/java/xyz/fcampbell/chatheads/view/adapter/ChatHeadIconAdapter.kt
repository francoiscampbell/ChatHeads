package xyz.fcampbell.chatheads.view.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick

/**
 * The RecyclerView adapter to adapt from ChatHeads to their icon
 */
internal class ChatHeadIconAdapter(
        @LayoutRes val iconLayout: Int,
        val delegate: Delegate
) : RecyclerView.Adapter<ChatHeadIconAdapter.ViewHolder>() {
    lateinit var chatHeadClickedListener: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val iconView = parent.context.layoutInflater.inflate(iconLayout, parent, false)
        return ViewHolder(iconView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        delegate.bindIcon(holder.itemView, position)
        holder.itemView.onClick { chatHeadClickedListener(position) }
    }

    override fun getItemCount(): Int = delegate.getIconCount()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Delegate {
        fun getIconCount(): Int
        fun bindIcon(icon: View, position: Int)
    }
}