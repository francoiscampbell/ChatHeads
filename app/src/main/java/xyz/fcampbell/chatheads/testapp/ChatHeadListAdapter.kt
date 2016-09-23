package xyz.fcampbell.chatheads.testapp

import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.view.View
import kotlinx.android.synthetic.main.layout_icon.view.*
import kotlinx.android.synthetic.main.layout_page.view.*
import org.jetbrains.anko.onClick
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

/**
 * Convenience class to adapt a List or array of ChatHeads. A basic ChatHead class is also included.
 * It is just a container for a Drawable for the icon and a View for the page.
 *
 * @param chatHeads The list of chat heads to show
 * @param iconLayout The layout to inflate to show one icon
 */
class ChatHeadListAdapter(
        private val chatHeads: List<ChatHead>,
        @LayoutRes iconLayout: Int,
        @LayoutRes pageLayout: Int
) : ChatHeadAdapter(iconLayout, pageLayout) {
    private var iconThumbPadding = 0

    override fun getItemCount(): Int = chatHeads.size

    override fun bindThumbnail(container: View) {
        setPadding(container)
    }

    /**
     * Bind data to an icon view.
     *
     * @param container The icon that will be shown. This is inflated from iconLayout
     * @param position The position of the chat head in the dataset
     */
    override fun bindIcon(container: View, position: Int) {
        setPadding(container)
        container.iconImage.setImageDrawable(chatHeads[position].icon)
    }

    private fun setPadding(container: View) {
        if (iconThumbPadding == 0) {
            iconThumbPadding = container.context.resources.getDimensionPixelSize(R.dimen.icon_thumb_padding)
        }
        container.setPadding(iconThumbPadding, iconThumbPadding, iconThumbPadding, iconThumbPadding)
    }

    /**
     * Provide a page for the expanded view.
     *
     * @param position The position of the chat head in the dataset
     * @return The View to show as a page for position
     */
    override fun bindPage(container: View, position: Int) {
        container.send.onClick {
            container.history.append(container.pageText.text.toString() + "\n")
        }
    }

    /**
     * A chat head. This is just a convenience class to be used with ChatHeadListAdapter.
     *
     * @param icon The icon to show in the top bar
     * @param text The text to show in the expanded panel
     */
    data class ChatHead(val icon: Drawable,
                        val text: String)
}