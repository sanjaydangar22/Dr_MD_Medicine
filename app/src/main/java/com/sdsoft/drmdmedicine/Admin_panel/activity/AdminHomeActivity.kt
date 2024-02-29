package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.sdsoft.drmdmedicine.Admin_panel.fragment.AdminHomeFragment
import com.sdsoft.drmdmedicine.Admin_panel.fragment.AdminProfileFragment
import com.sdsoft.drmdmedicine.Admin_panel.fragment.MedicineFragment
import com.sdsoft.drmdmedicine.Admin_panel.fragment.PatientListFragment
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.LoginOptionActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityAdminHomeBinding
import com.sdsoft.drmdmedicine.fragment.HomeFragment
import com.sdsoft.drmdmedicine.fragment.MyMedicineFragment
import com.sdsoft.drmdmedicine.fragment.ProfileFragment
import com.sdsoft.drmdmedicine.fragment.ReportFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminHomeActivity : BaseActivity(R.layout.activity_admin_home) {
    lateinit var adminHomeBinding: ActivityAdminHomeBinding
    lateinit var fragment: Fragment
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminHomeBinding= ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(adminHomeBinding.root)
        auth = Firebase.auth

        navView()
        initView()
        bottomNavigation()
    }

    private fun navView() {
        adminHomeBinding.imgMenu.setOnClickListener {
            adminHomeBinding.drawerView.openDrawer(GravityCompat.START)

        }



//        Share App
        adminHomeBinding.layShareNav.setOnClickListener {
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

        adminHomeBinding.layLogoutNav.setOnClickListener {



            var myEdit: SharedPreferences.Editor = doctorsharedPreferences.edit()
            myEdit.remove("isLogin")
            myEdit.commit()
            auth.signOut()
            Toast.makeText(this@AdminHomeActivity, "Logout", Toast.LENGTH_SHORT).show()
            var i = Intent(this@AdminHomeActivity, LoginOptionActivity::class.java)
            startActivity(i)
            finish()


        }


    }
    private fun bottomNavigation() {

        supportFragmentManager.beginTransaction().replace(R.id.frameContent, AdminHomeFragment())
            .commit()

        adminHomeBinding.bottomNavAdmin.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.bottom_Home -> {
                    fragment = AdminHomeFragment()
                    callFragment(fragment)
                    adminHomeBinding.txtTitle.text = "Home"
                }

                R.id.bottom_PatientList -> {
                    fragment = PatientListFragment()
                    callFragment(fragment)

                    adminHomeBinding.txtTitle.text = "Patient List"
                }

                R.id.bottom_Medicine -> {
                    fragment = MedicineFragment()
                    callFragment(fragment)

                    adminHomeBinding.txtTitle.text = "Medicine"
                }

                R.id.bottom_Profile -> {
                    fragment = AdminProfileFragment()
                    callFragment(fragment)

                    adminHomeBinding.txtTitle.text = "Profile"
                }


            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    //loading the another fragment in viewPager
    private fun callFragment(fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.frameContent, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun initView() {

    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)


    }
}