package xyz.fcampbell.chatheads.testapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import xyz.fcampbell.chatheads.ChatHeadService
import xyz.fcampbell.chatheads.view.ChatHeadView
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

class MainActivity : AppCompatActivity() {

    private var boundToService = false

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            boundToService = true

            val chatHeadService = (service as ChatHeadService.LocalBinder).service

            addView.onClick {
                val chatHeadView = ChatHeadView(this@MainActivity)
                chatHeadView.initialize(prepareDummyChatHeads())
                chatHeadService.attachView(chatHeadView)
                chatHeadService.openChatHeads()
            }

            removeView.onClick {
                chatHeadService.detachView()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindService(Intent(this, ChatHeadService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)

//        val chatHeadView = ChatHeadView(this)
//        chatHeadView.initialize(prepareDummyChatHeads())
//        setContentView(chatHeadView)
//        chatHeadView.open()
    }

    fun prepareDummyChatHeads(): ChatHeadAdapter {
        val icon = resources.getDrawable(R.drawable.ic_filter_1_black_48dp, null)
        val chatHeads = (0..23).map { ChatHeadListAdapter.ChatHead(icon, "Chat head $it") }.toList()

        return ChatHeadListAdapter(chatHeads, R.layout.layout_icon, R.layout.layout_page, R.id.iconImage, R.id.pageText)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (boundToService) unbindService(serviceConnection)
    }
}
