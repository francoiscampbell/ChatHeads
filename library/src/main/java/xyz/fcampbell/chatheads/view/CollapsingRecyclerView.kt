package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * LinearLayoutManager than can collapse all its items onto the first one
 */
internal class CollapsingRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    fun collapse() {
        forEachChildIndexed { child, index ->
            child.animateChildTranslation(-child.x - x, -child.y - y)
        }
    }

    fun expand() {
        forEachChildIndexed { child, index ->
            child.animateChildTranslation(0f)
        }

    }

    fun View.animateChildTranslation(targetTranslation: Float) = animateChildTranslation(targetTranslation, targetTranslation)

    fun View.animateChildTranslation(targetTranslationX: Float, targetTranslationY: Float) {
        animate().translationX(targetTranslationX)
                .translationY(targetTranslationY)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator(0.5f))
                .start()
    }

    inline fun forEachChildIndexed(action: (View, Int) -> Unit) {
        for (i in 0..childCount - 1) action(getChildAt(i), i)
    }
}