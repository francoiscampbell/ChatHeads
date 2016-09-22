package xyz.fcampbell.chatheads.view.impl

import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import android.widget.ImageView
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * Convenience class to adapt a List or array of ChatHeads. A basic ChatHead class is also included.
 * It is just a container for a Drawable for the icon and a View for the page.
 *
 * @param chatHeads The list of chat heads to show
 * @param iconLayout The layout to inflate to show one icon
 * @param iconImageId The id of an ImageView to show the chat head's icon
 */
class ChatHeadListAdapter(
        private val chatHeads: List<ChatHead>,
        thumbnail: View?,
        @LayoutRes iconLayout: Int,
        @IdRes private val iconImageId: Int
) : ChatHeadAdapter(thumbnail, iconLayout) {

    /**
     * @return The number of chat heads in your dataset
     */
    override fun getChatHeadCount() = chatHeads.size

    /**
     * Bind data to an icon view.
     *
     * @param icon The icon that will be shown. This is inflated from iconLayout
     * @param position The position of the chat head in the dataset
     */
    override fun bindIcon(icon: View, position: Int) {
        (icon.findViewById(iconImageId) as ImageView).setImageDrawable(chatHeads[position].icon)
    }

    /**
     * Provide a page for the expanded view.
     *
     * @param position The position of the chat head in the dataset
     * @return The View to show as a page for position
     */
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