package xyz.fcampbell.chatheads.view

import android.graphics.drawable.Drawable
import android.view.View

/**
 * A chat head
 *
 * @param icon The icon to show in the top bar
 * @param page The view to show in the expanded panel
 */
data class ChatHead(val icon: Drawable,
                    val page: View)