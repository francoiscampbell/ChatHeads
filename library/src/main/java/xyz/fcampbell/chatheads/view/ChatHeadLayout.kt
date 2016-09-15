package xyz.fcampbell.chatheads.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Root layout that must be the parent of whatever will be used as a chat head.
 */
class ChatHeadLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(
        context,
        attrs,
        defStyleAttr) {
}