package xyz.fcampbell.chatheads.testapp

import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import xyz.fcampbell.chatheads.ChatHeadService
import xyz.fcampbell.chatheads.view.ChatHead
import xyz.fcampbell.chatheads.view.ChatHeadAdapter
import xyz.fcampbell.chatheads.view.ChatHeadView

class MainActivity : AppCompatActivity() {

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val chatHeadService = (service as ChatHeadService.LocalBinder).service

            addView.onClick {
                val chatHeadView = ChatHeadView(this@MainActivity)
                chatHeadView.initialize(prepareDummyChatHeads())
                chatHeadService.show(chatHeadView)
            }

            removeView.onClick {
                chatHeadService.hide()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatHeadView = ChatHeadView(this@MainActivity)
        chatHeadView.initialize(prepareDummyChatHeads())
        setContentView(chatHeadView)

//        bindService(Intent(this, ChatHeadService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun prepareDummyChatHeads(): ChatHeadAdapter {
        val dummyDrawable = resources.getDrawable(R.drawable.ic_android_black_48dp, null)
        val chatHeads = listOf(
                ChatHead(dummyDrawable, TextView(this).apply { text = "Chat head 1"; background = ColorDrawable(Color.RED) }),
                ChatHead(dummyDrawable, TextView(this).apply { text = "Chat head 2"; background = ColorDrawable(Color.BLUE) }),
                ChatHead(dummyDrawable, TextView(this).apply { text = "Chat head 3"; background = ColorDrawable(Color.WHITE) }))


        return ChatHeadAdapter(chatHeads)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
