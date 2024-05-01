package com.example.calchue

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    companion object {
        const val ANIMATION_TIME: Long = 1300 // animation time
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(this.mainLooper).postDelayed({ // delays showing MainActivity
            startActivity(Intent(this, MainActivity::class.java)) // starts MainActivity after ANIMATION_TIME
            finish()
        }, ANIMATION_TIME)
    }
}

// with the help from: https://rex50.medium.com/amazing-animated-splash-screen-kotlin-and-lottie-how-to-b98504005abf
// and LottieFiles for the animation