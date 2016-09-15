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
import xyz.fcampbell.chatheads.testapp.util.inflate
import xyz.fcampbell.chatheads.view.ChatHeadLayout

class MainActivity : AppCompatActivity() {

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val chatHeadService = (service as ChatHeadService.LocalBinder).service

            val testView = layoutInflater.inflate(R.layout.layout_chat_test) as ChatHeadLayout
            addView.onClick { chatHeadService.addChatHead(testView) }
            removeView.onClick { chatHeadService.removeChatHead(testView) }
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(Intent(this, ChatHeadService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
