package com.mahakalinfoways.drmdclinic

import android.content.Intent
import android.os.Bundle
import android.os.Handler


class SplashScreenActivity : BaseActivity(R.layout.activity_splash_screen) {

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