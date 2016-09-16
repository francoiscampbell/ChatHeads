package xyz.fcampbell.chatheads.view

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Adapts a ChatHead's page property into a ViewPager
 */
class ChatHeadPagerAdapter(val chatHeads: List<ChatHead>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int) = chatHeads[position].page

    override fun getCount() = chatHeads.size

    override fun isViewFromObject(view: View, `object`: Any) = view === `object`
}