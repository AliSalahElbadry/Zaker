package com.example.zekr

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    lateinit var player: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsController = WindowCompat.getInsetsController(
            window, window.decorView
        )
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        setContentView(R.layout.activity_splash)

        player= MediaPlayer.create(this,R.raw.caver)

        lifecycleScope.launch {
            launch {
                player.start()
                delay(32000)
            }.join()
            launch {
                val i = Intent(this@Splash, MainActivity::class.java)
                startActivity(i)
                finish()
            }
        }
    }
}