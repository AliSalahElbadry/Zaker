package com.example.zekr

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import kotlinx.coroutines.MainScope
import java.util.Random
import java.util.Timer
import java.util.TimerTask

class ZekrService : Service() {
    private val sounds = IntArray(25)
    private val texts = IntArray(15)
    lateinit var messageLayout:View
    var isSet = false
    private var turn = 1
    override fun onCreate() {
        super.onCreate()
        sounds[0] = R.raw.i2
        sounds[1] = R.raw.i3
        sounds[2] = R.raw.i4
        sounds[3] = R.raw.i5
        sounds[5] = R.raw.i6
        sounds[6] = R.raw.i7
        sounds[7] = R.raw.i8
        sounds[8] = R.raw.i9
        sounds[9] = R.raw.i10
        sounds[10] = R.raw.i11
        sounds[11] = R.raw.i12
        sounds[12] = R.raw.i13
        sounds[13] = R.raw.i14
        sounds[14] = R.raw.i15
        sounds[15] = R.raw.i16
        sounds[16] = R.raw.i17
        sounds[17] = R.raw.i18
        sounds[18] = R.raw.i19
        sounds[19] = R.raw.i20
        sounds[20] = R.raw.i21
        sounds[21] = R.raw.i22
        sounds[22] = R.raw.i23
        sounds[23] = R.raw.i24
        sounds[24] = R.raw.i25
        texts[0]=R.string.t1
        texts[1]=R.string.t2
        texts[2]=R.string.t3
        texts[3]=R.string.t4
        texts[4]=R.string.t6
        texts[5]=R.string.t7
        texts[6]=R.string.t8
        texts[7]=R.string.t9
        texts[8]=R.string.t10
        texts[9]=R.string.t11
        texts[10]=R.string.t12
        texts[11]=R.string.t13
        texts[12]=R.string.t14
        texts[13]=R.string.t15
        texts[14]=R.string.t16
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
     fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("التطبيق يعمل في الخلفية")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)

            .build()
        startForeground(2, notification)
    }


     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return Service.START_STICKY
    }


     override fun onDestroy() {
         val broadcastIntent = Intent()
         broadcastIntent.action = "restartservice"
         broadcastIntent.setClass(this, Restarter::class.java)
         this.sendBroadcast(broadcastIntent)
         stoptimertask()
        super.onDestroy()
    }


    private var timer: Timer? = null
    private var timerTask: TimerTask? =null
     fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                if(turn ==0)
                {
                    turn=1
                    playPlayer()
                }else{
                    turn = 0
                    Handler(Looper.getMainLooper()).post {
                        showOnScreen()
                    }
                }
            }
        }
        timer?.schedule(timerTask, (1000 * 60 * 1).toLong(), (1000 * 60 * 1).toLong()) //
    }
    fun playPlayer()
    {
        val z = Random().nextInt(24)
        MediaPlayer.create(baseContext, if (z > 0) sounds[z] else sounds[z * -1]).start()
    }
    @SuppressLint("MissingPermission")
    fun showOnScreen()
    {
        val z = Random().nextInt(15)
        if(isSet&&messageLayout.isVisible)
        {
           messageLayout.visibility = View.GONE
        }
         messageLayout=
            LayoutInflater.from(this@ZekrService).inflate(R.layout.fragment_zekr_text,null)
            val messageHolder=messageLayout.findViewById<TextView>(R.id.textViewMessage)
            messageHolder.text= getString(if(z>0)texts[z] else texts[0])
            messageHolder.setOnClickListener{
                messageLayout.visibility= View.GONE
            }
            val windowManager=this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val layoutParams=WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                                                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                                        else WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT
            )
            layoutParams.verticalMargin = 0.08F
            layoutParams.gravity= Gravity.TOP xor Gravity.END
            windowManager.addView(messageLayout,layoutParams)
        val animation = TranslateAnimation(1000F,0F,messageLayout.translationY,messageLayout.translationY)
        animation.duration = 1500
        messageLayout.animation = animation
        messageLayout.visibility = View.VISIBLE
        animation.start()
        isSet = true
    }

     fun stoptimertask() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

     override fun onBind(intent: Intent?): IBinder? {
         return null
     }
}