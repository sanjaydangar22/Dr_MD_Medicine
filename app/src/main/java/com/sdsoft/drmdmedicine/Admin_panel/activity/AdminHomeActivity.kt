package com.sdsoft.drmdmedicine.Admin_panel.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sdsoft.drmdmedicine.Admin_panel.fragment.AdminHomeFragment
import com.sdsoft.drmdmedicine.Admin_panel.fragment.AdminProfileFragment
import com.sdsoft.drmdmedicine.Admin_panel.fragment.MedicineFragment
import com.sdsoft.drmdmedicine.Admin_panel.fragment.PatientListFragment
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityAdminHomeBinding
import com.sdsoft.drmdmedicine.fragment.HomeFragment
import com.sdsoft.drmdmedicine.fragment.MyMedicineFragment
import com.sdsoft.drmdmedicine.fragment.ProfileFragment
import com.sdsoft.drmdmedicine.fragment.ReportFragment

class AdminHomeActivity : AppCompatActivity() {
    lateinit var adminHomeBinding: ActivityAdminHomeBinding
    lateinit var fragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminHomeBinding= ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(adminHomeBinding.root)

        initView()
        bottomNavigation()
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
}