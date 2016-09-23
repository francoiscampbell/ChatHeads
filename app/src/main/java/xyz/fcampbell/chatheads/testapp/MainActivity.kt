package xyz.fcampbell.chatheads.testapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import xyz.fcampbell.chatheads.ChatHeadService
import xyz.fcampbell.chatheads.view.adapter.ChatHeadAdapter

class MainActivity : AppCompatActivity() {

    private var boundToService = false

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            boundToService = true

            val chatHeadService = (service as ChatHeadService.LocalBinder).service
            chatHeadService.initialize(prepareDummyChatHeads(), R.style.AppTheme)

            addView.onClick {
                chatHeadService.initialize(prepareDummyChatHeads(), R.style.AppTheme)
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
    }

    fun prepareDummyChatHeads(): ChatHeadAdapter {
        val icon = ActivityCompat.getDrawable(this, R.drawable.ic_filter_1_black_48dp)
        val chatHeads = (0..23).map { ChatHeadListAdapter.ChatHead(icon, "Chat head $it") }.toList()

        return ChatHeadListAdapter(chatHeads, R.layout.layout_icon, R.layout.layout_page)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (boundToService) unbindService(serviceConnection)
    }
}
