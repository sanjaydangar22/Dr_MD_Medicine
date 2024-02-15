package com.sdsoft.drmdmedicine

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminHomeActivity


class SplashScreenActivity : AppCompatActivity() {

     val SPLASH_TIME_OUT = 3000L // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initView()
    }

    private fun initView() {
       Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, LoginOptionActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)
    }
}