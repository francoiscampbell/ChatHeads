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
        val chatHeads: List<ChatHead>
) : RecyclerView.Adapter<ChatHeadIconAdapter.ChatHeadViewHolder>() {
    lateinit var chatHeadClickedListener: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatHeadViewHolder(ImageView(parent.context))

    override fun onBindViewHolder(holder: ChatHeadViewHolder, position: Int) {
        holder.bindIcon(chatHeads[position].icon)
        holder.itemView.onClick { chatHeadClickedListener(position) }
    }

    override fun getItemCount(): Int = chatHeads.size

    inner class ChatHeadViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {
        fun bindIcon(icon: Drawable) = (itemView as ImageView).setImageDrawable(icon)
    }
}