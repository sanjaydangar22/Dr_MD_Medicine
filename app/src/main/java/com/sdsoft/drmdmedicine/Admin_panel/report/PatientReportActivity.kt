package com.sdsoft.drmdmedicine.Admin_panel.report

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientDataViewBinding
import com.sdsoft.drmdmedicine.databinding.ActivityPatientReportBinding

class PatientReportActivity : AppCompatActivity() {
    lateinit var patientDataViewBinding: ActivityPatientReportBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientDataViewBinding = ActivityPatientReportBinding.inflate(layoutInflater)
        setContentView(patientDataViewBinding.root)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {

        patientDataViewBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
        var patientUid = intent.getStringExtra("patientUid")

        Log.e("TAG", "patientUid:  $patientUid ")


        mDbRef.child("PatientList").child(patientUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val patientItem = snapshot.getValue(PatientModelClass::class.java)
                        if (patientItem != null) {
                            // User data retrieved successfully
                            patientUid = patientItem.patientUid
                            val patientImage = patientItem.patientImage
                            val patientName = patientItem.patientName


                            Log.e("TAG", "patientImage:  $patientImage ")

                            Glide.with(this@PatientReportActivity).load(patientImage)
                                .placeholder(R.drawable.ic_image)
                                .into(patientDataViewBinding.imgPatientImage)
                            patientDataViewBinding.txtPatientName.text = patientName.toString()



                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(
                            this@PatientReportActivity,
                            "Data not Found",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })



    }
}