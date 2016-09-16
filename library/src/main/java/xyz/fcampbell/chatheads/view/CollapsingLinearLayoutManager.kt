package xyz.fcampbell.chatheads.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * LinearLayoutManager than can collapse all its items onto the first one
 */
class CollapsingLinearLayoutManager(
        context: Context,
        orientation: Int,
        reverseLayout: Boolean
) : LinearLayoutManager(
        context,
        orientation,
        reverseLayout) {

    fun collapse() {
        val firstChildX = getChildAt(0).x
        val firstChildY = getChildAt(0).y

        forEachChild { it.animateChildTranslation(firstChildX - it.x, firstChildY - it.y) }
    }

    fun expand() {
        forEachChild { it.animateChildTranslation(0f) }
    }

    fun View.animateChildTranslation(targetTranslation: Float) = animateChildTranslation(targetTranslation, targetTranslation)

    fun View.animateChildTranslation(targetTranslationX: Float, targetTranslationY: Float) {
        animate().translationX(targetTranslationX)
                .translationY(targetTranslationY)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator(0.5f))
                .start()
    }

    inline fun forEachChild(action: (View) -> Unit) {
        for (i in 0..childCount - 1) action(getChildAt(i))
    }
}