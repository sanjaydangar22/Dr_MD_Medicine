package com.sdsoft.drmdmedicine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sdsoft.drmdmedicine.databinding.ActivityMainBinding
import com.sdsoft.drmdmedicine.fragment.HomeFragment
import com.sdsoft.drmdmedicine.fragment.MyMedicineFragment
import com.sdsoft.drmdmedicine.fragment.ProfileFragment
import com.sdsoft.drmdmedicine.fragment.ReportFragment

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    lateinit var fragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initView()
        bottomNavigation()
    }

    private fun bottomNavigation() {

        supportFragmentManager.beginTransaction().replace(R.id.frameContent, HomeFragment())
            .commit()

        mainBinding.bottomNav.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.bottom_Home -> {
                    fragment = HomeFragment()
                    callFragment(fragment)
                    mainBinding.txtTitle.text = "Home"
                }

                R.id.bottom_Report -> {
                    fragment = ReportFragment()
                    callFragment(fragment)

                    mainBinding.txtTitle.text = "Report"
                }

                R.id.bottom_Medicine -> {
                    fragment = MyMedicineFragment()
                    callFragment(fragment)

                    mainBinding.txtTitle.text = "My Medicine"
                }

                R.id.bottom_Profile -> {
                    fragment = ProfileFragment()
                    callFragment(fragment)

                    mainBinding.txtTitle.text = "Profile"
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