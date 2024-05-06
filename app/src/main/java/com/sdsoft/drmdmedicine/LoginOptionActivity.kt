package com.sdsoft.drmdmedicine

import android.content.Intent
import android.os.Bundle
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminHomeActivity
import com.sdsoft.drmdmedicine.databinding.ActivityLoginOptionBinding

class LoginOptionActivity : BaseActivity(R.layout.activity_login_option) {

    lateinit var binding:ActivityLoginOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (loginSharedPreferences.getBoolean("isLogin", false) == true) {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()

        }
        initView()
    }

    private fun initView() {
        binding.relDoctorLogin.setOnClickListener{
            var i=Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.relStaffLogin.setOnClickListener{
            var i=Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}