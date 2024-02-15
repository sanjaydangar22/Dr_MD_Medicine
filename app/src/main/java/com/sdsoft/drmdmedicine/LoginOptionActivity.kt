package com.sdsoft.drmdmedicine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminLoginActivity
import com.sdsoft.drmdmedicine.databinding.ActivityLoginOptionBinding

class LoginOptionActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.relDoctorLogin.setOnClickListener{
            var i=Intent(this,AdminLoginActivity::class.java)
            startActivity(i)

        }
    }
}