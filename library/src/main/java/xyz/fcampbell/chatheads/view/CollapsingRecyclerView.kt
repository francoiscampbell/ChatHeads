package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * RecyclerView than can collapseWithAnimation all its items onto the first one
 */
internal class CollapsingRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    fun collapseWithAnimation() {
        forEachChildIndexed { child, index ->
            child.animate()
                    .translationX(-child.x)
                    .translationY(-child.y)
                    .alpha(0f)
                    .setDuration(ChatHeadOrchestrator.CLOSE_ANIMATION_DURATION)
                    .setInterpolator(ChatHeadOrchestrator.ANIMATION_INTERPOLATOR)
                    .withEndAction { visibility = View.GONE }
                    .start()
        }
    }

    fun expandWithAnimation() {
        forEachChildIndexed { child, index ->
            child.animate()
                    .translationX(0f)
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(ChatHeadOrchestrator.OPEN_ANIMATION_DURATION)
                    .setInterpolator(ChatHeadOrchestrator.ANIMATION_INTERPOLATOR)
                    .withStartAction { visibility = View.VISIBLE }
                    .start()
        }
    }

    inline fun forEachChildIndexed(action: (View, Int) -> Unit) {
        for (i in 0..childCount - 1) action(getChildAt(i), i)
    }
}