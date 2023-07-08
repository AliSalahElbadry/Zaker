package com.example.zekr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Restarter : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
       val mode = context.applicationContext.getSharedPreferences("Zaker", AppCompatActivity.MODE_PRIVATE).getInt("key", 0)
        if(mode==1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, ZekrService::class.java))
            } else {
                context.startService(Intent(context, ZekrService::class.java))
            }
        }
    }
}