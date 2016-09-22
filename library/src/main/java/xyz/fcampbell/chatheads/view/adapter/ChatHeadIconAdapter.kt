package xyz.fcampbell.chatheads.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * The RecyclerView adapter to adapt from ChatHeads to their icon
 */
internal class ChatHeadIconAdapter(
        @LayoutRes val iconLayout: Int,
        val delegate: Delegate
) : RecyclerView.Adapter<ChatHeadIconAdapter.ViewHolder>() {
    lateinit var chatHeadClickedListener: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val iconView = layoutInflater.inflate(iconLayout, parent, false)
        return ViewHolder(iconView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        delegate.bindIcon(holder.itemView, position)
        holder.itemView.setOnClickListener { chatHeadClickedListener(position) }
    }

    override fun getItemCount(): Int = delegate.getItemCount()

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Delegate {
        fun getItemCount(): Int
        fun bindIcon(icon: View, position: Int)
    }
}