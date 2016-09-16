package xyz.fcampbell.chatheads.view.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Adapts a ChatHead's page property into a ViewPager
 */
internal class ChatHeadPagerAdapter(val delegate: Delegate) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val page = delegate.getPage(position)
        container.addView(page)
        return page
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)

    override fun getCount() = delegate.getPageCount()

    override fun isViewFromObject(view: View, `object`: Any) = view === `object`

    interface Delegate {
        fun getPageCount(): Int
        fun getPage(position: Int): View
    }
}