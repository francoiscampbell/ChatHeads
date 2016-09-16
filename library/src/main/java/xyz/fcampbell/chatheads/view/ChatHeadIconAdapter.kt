package xyz.fcampbell.chatheads.view

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import org.jetbrains.anko.onClick

/**
 * The RecyclerView adapter to adapt from ChatHeads to their icon
 */
class ChatHeadIconAdapter(
        val delegate: Delegate
) : RecyclerView.Adapter<ChatHeadIconAdapter.ViewHolder>() {
    lateinit var chatHeadClickedListener: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(ImageView(parent.context))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindIcon(delegate.getIcon(position))
        holder.itemView.onClick { chatHeadClickedListener(position) }
    }

    override fun getItemCount(): Int = delegate.getIconCount()

    class ViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {
        fun bindIcon(icon: Drawable) = (itemView as ImageView).setImageDrawable(icon)
    }

    interface Delegate {
        fun getIconCount(): Int
        fun getIcon(position: Int): Drawable
    }
}