package com.example.busmapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoTextView = findViewById<TextView>(R.id.logo_textView)
        val animation = AnimationUtils.loadAnimation(this,R.anim.fade_in)

        logoTextView.startAnimation(animation)

        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed(Runnable {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        },1500)

    }
}