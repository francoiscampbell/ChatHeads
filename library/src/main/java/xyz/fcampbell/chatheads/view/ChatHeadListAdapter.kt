package xyz.fcampbell.chatheads.view

import android.graphics.drawable.Drawable
import android.view.View

/**
 * Convenience class to adapt a List or array of ChatHeads
 */
class ChatHeadListAdapter(private val chatHeads: List<ChatHead>) : ChatHeadAdapter() {
    constructor(chatHeads: Array<ChatHead>) : this(chatHeads.toList())

    override fun getChatHeadCount() = chatHeads.size
    override fun getIcon(position: Int) = chatHeads[position].icon
    override fun getPage(position: Int) = chatHeads[position].page

    /**
     * A chat head. This is just a convenience class to be used with ChatHeadListAdapter.
     *
     * @param icon The icon to attachView in the top bar
     * @param page The view to attachView in the expanded panel
     */
    data class ChatHead(val icon: Drawable,
                        val page: View)
}