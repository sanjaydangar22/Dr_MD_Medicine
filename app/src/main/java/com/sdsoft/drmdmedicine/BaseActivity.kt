package com.sdsoft.drmdmedicine

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminHomeActivity


abstract class BaseActivity(view: Int) : AppCompatActivity() {

    private var layoutView = view
    lateinit var progressBarDialog: ProgressBarDialog
    lateinit var doctorsharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutView)

        progressBarDialog = ProgressBarDialog(this)

        doctorsharedPreferences = getSharedPreferences("DoctorSharePref", AppCompatActivity.MODE_PRIVATE)
        if (doctorsharedPreferences.getBoolean("isLogin", false) == true) {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()

        }
    }


}