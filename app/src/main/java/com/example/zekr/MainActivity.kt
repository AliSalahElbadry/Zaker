package com.example.zekr

import android.app.ActivityManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    var mode: Int = 0
    private lateinit var  preferences:SharedPreferences
   private lateinit var button:Button
   private lateinit var radioGroup: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsController = WindowCompat.getInsetsController(
            window, window.decorView
        )
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        radioGroup = findViewById(R.id.group)
        preferences = getSharedPreferences("Zaker", MODE_PRIVATE)
        mode = preferences.getInt("key", 0)
        val type = preferences.getInt("type",0)
        radioGroup.check(if(type==0)R.id.radioButtonVoice else R.id.radioButtonText)
        preferences.edit().putInt("type",type).apply()
        Type.type=type
        if (mode == 0) {
            button.setText(R.string.btns)
        } else if (mode == 1) {
            if (!isMyServiceRunning(ZekrService::class.java)) {
                val mServiceIntent = Intent(this, ZekrService::class.java)
                startService(mServiceIntent)
            }
            button.setText(R.string.btne)
        }
        button.setOnClickListener {
            val mYourService = ZekrService()
            val mServiceIntent = Intent(this, mYourService::class.java)
            if (mode==0) {
                if (!isMyServiceRunning(mYourService::class.java)) {
                    startService(mServiceIntent)
                } else {
                    stopService(mServiceIntent)
                    startService(mServiceIntent)
                }
                mode = 1
                button.setText(R.string.btne)
                Toast.makeText(baseContext, "يعمل", Toast.LENGTH_SHORT).show()
            } else if (mode==1) {
                mode = 0
                stopService(mServiceIntent)
                button.setText(R.string.btns)
                Toast.makeText(baseContext, "لا يعمل", Toast.LENGTH_SHORT).show()
            }
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.radioButtonVoice)
            {
                preferences.edit().putInt("type",0).apply()
                Type.type=0
            }else if(checkedId == R.id.radioButtonText)
            {
                preferences.edit().putInt("type",1).apply()
                Type.type=1
            }
            Toast.makeText(baseContext,"تم التحويل",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!Settings.canDrawOverlays(applicationContext)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            Toast.makeText(this, "الاذن مطلوب",Toast.LENGTH_SHORT).show()
            startActivityForResult(intent, 155)
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== 155)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"تم منح الاذن",Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        preferences.edit().putInt("key",mode).apply()
    }

    override fun onDestroy() {
        if(mode==1) {
            val broadcastIntent = Intent()
            broadcastIntent.action = "restartservice"
            broadcastIntent.setClass(this, Restarter::class.java)
            this.sendBroadcast(broadcastIntent)
        }
        super.onDestroy()
    }
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}