package com.sdsoft.drmdmedicine.Admin_panel.Patient_data_view

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
import com.sdsoft.drmdmedicine.Admin_panel.Patient_data_view.medicine.PatientMedicineActivity
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.Admin_panel.Patient_data_view.report.PatientReportActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientDataViewBinding

class PatientDataViewActivity : AppCompatActivity() {

    lateinit var patientDataViewBinding: ActivityPatientDataViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        patientDataViewBinding = ActivityPatientDataViewBinding.inflate(layoutInflater)
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
                            var patientAge = patientItem.patientAge
                            var patientGender = patientItem.patientGender
                            var patientMobileNo = patientItem.patientMobileNo

                            Log.e("TAG", "patientImage:  $patientImage ")

                            Glide.with(this@PatientDataViewActivity).load(patientImage)
                                .placeholder(R.drawable.ic_image)
                                .into(patientDataViewBinding.imgPatientImage)
                            patientDataViewBinding.txtPatientName.text = patientName.toString()
                            patientDataViewBinding.txtPatientAge.text = patientAge.toString()
                            patientDataViewBinding.txtPatientGender.text = patientGender.toString()
                            patientDataViewBinding.txtPatientMobileNo.text =
                                patientMobileNo.toString()


                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(
                            this@PatientDataViewActivity,
                            "Data not Found",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        patientDataViewBinding.cdReport.setOnClickListener {
            var i = Intent(this, PatientReportActivity::class.java)
            i.putExtra("patientUid", patientUid)
            startActivity(i)
        }

        patientDataViewBinding.cdMedicine.setOnClickListener {
            var i = Intent(this, PatientMedicineActivity::class.java)
            i.putExtra("patientUid", patientUid)
            startActivity(i)
        }
    }
}