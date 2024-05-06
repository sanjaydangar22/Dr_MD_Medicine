package com.sdsoft.drmdmedicine.staff_panel

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.sdsoft.drmdmedicine.Admin_panel.activity.AppointmentsActivity
import com.sdsoft.drmdmedicine.Admin_panel.activity.CompletedAppointmentsActivity
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.LoginOptionActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityStaffHomeBinding

class StaffHomeActivity : BaseActivity(R.layout.activity_staff_home) {
    lateinit var binding: ActivityStaffHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        mDbRef = FirebaseDatabase.getInstance().reference
        navView()
        initView()
    }
    private fun navView() {
        binding.imgMenu.setOnClickListener {
            binding.drawerView.openDrawer(GravityCompat.START)

        }



//        Share App
        binding.layShareNav.setOnClickListener {
            val appPackageName: String = getPackageName()
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out the App at: https://play.google.com/store/apps/details?id=$appPackageName"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
//Logout

        binding.layLogoutNav.setOnClickListener {



            var myEdit: SharedPreferences.Editor = loginSharedPreferences.edit()
            myEdit.remove("isLogin")
            myEdit.commit()
            auth.signOut()
            Toast.makeText(this@StaffHomeActivity, "Logout", Toast.LENGTH_SHORT).show()
            var i = Intent(this@StaffHomeActivity, LoginOptionActivity::class.java)
            startActivity(i)
            finish()


        }


    }
    private fun initView() {
        binding.cdNewAppointments.setOnClickListener {
            var i = Intent(this, AppointmentsActivity::class.java)
            i.putExtra("userType","Staff")
            startActivity(i)
        }
        binding.cdAppointmentsCompleted.setOnClickListener {
            var i = Intent(this, CompletedAppointmentsActivity::class.java)
            i.putExtra("userType","Staff")
            startActivity(i)
        }
    }
}