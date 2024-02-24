package com.sdsoft.drmdmedicine

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminHomeActivity


abstract class BaseActivity(view: Int) : AppCompatActivity() {

    private var layoutView = view
    lateinit var progressBarDialog: ProgressBarDialog
    lateinit var doctorsharedPreferences:SharedPreferences

    lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutView)
        mDbRef = FirebaseDatabase.getInstance().reference
        progressBarDialog = ProgressBarDialog(this)

        doctorsharedPreferences = getSharedPreferences("DoctorSharePref", AppCompatActivity.MODE_PRIVATE)

    }


}