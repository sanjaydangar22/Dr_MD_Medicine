package com.sdsoft.drmdmedicine

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminHomeActivity
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


abstract class BaseActivity(view: Int) : AppCompatActivity() {

    private var layoutView = view
    lateinit var progressBarDialog: ProgressBarDialog
    lateinit var loginSharedPreferences: SharedPreferences

    lateinit var mDbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutView)
        mDbRef = FirebaseDatabase.getInstance().reference
        progressBarDialog = ProgressBarDialog(this)

        loginSharedPreferences =
            getSharedPreferences("LoginSharePref", AppCompatActivity.MODE_PRIVATE)


        dateCheckAndRemoveValue()
        permissionFun()
    }

    private fun dateCheckAndRemoveValue() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val formattedCurrentDate = dateFormat.format(currentDate)

        mDbRef.child("TodayDate").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storedDate = snapshot.getValue(String::class.java)

                if (storedDate != null && storedDate != formattedCurrentDate) {
                    mDbRef.child("TodayDate").setValue(formattedCurrentDate)
                    mDbRef.child("AppointmentNumber").setValue(0)
                    mDbRef.child("AppointmentList").removeValue()
                    mDbRef.child("AppointmentCompletedList").removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }


    fun permissionFun() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (checkPermissionVersion()) {
//                Toast.makeText(this, "Permission already granted .", Toast.LENGTH_LONG).show()

            } else {

                requestPermission()

            }
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            if (checkPermission()) {

//                Toast.makeText(this, "Permission already granted .", Toast.LENGTH_LONG).show()

            } else {

                requestPermission()

            }
        }
    }

    private fun checkPermission(): Boolean {

        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result1 =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val result2 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CAMERA
        )

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionVersion(): Boolean {

        val result2 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CAMERA
        )

        return result2 == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (Environment.isExternalStorageManager()) {
                startActivity(Intent(this, SplashScreenActivity::class.java))
            } else { //request for the permission
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ),
                    200
                )
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                100
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> if (grantResults.size > 0) {

                val writeExternalStorage =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readExternalStorage =
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                val camera = grantResults[2] == PackageManager.PERMISSION_GRANTED

                if (writeExternalStorage && readExternalStorage && camera)
                    Toast.makeText(
                        this,
                        "Permission Granted",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    Toast.makeText(
                        this,
                        "Permission Denied",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            200 -> if (grantResults.size > 0) {
                val camera = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val image = grantResults[1] == PackageManager.PERMISSION_GRANTED


                if (camera && image)
                    Toast.makeText(
                        this,
                        "Permission Granted",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    Toast.makeText(
                        this,
                        "Permission Denied",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        }
    }


}