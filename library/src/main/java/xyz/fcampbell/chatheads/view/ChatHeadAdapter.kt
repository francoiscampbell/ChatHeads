package xyz.fcampbell.chatheads.view

/**
 * Adapts a data set into an icon and a page to be shown when the icon is expanded
 */
class ChatHeadAdapter(chatHeads: List<ChatHead>) {
    val iconAdapter = ChatHeadIconAdapter(chatHeads)
    val pageAdapter = ChatHeadPagerAdapter(chatHeads)
}