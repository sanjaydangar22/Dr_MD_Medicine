package com.sdsoft.drmdmedicine.Admin_panel.Patient_data_view.medicine


import android.content.Intent
import android.net.Uri
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
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminHomeActivity
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.ViewPagerAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientMedicineAddBinding
import java.util.UUID

class PatientMedicineAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityPatientMedicineAddBinding
    lateinit var progressBarDialog: ProgressBarDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    var patientUid: String? = null
    var frontImage: String? = null
    var backImage: String? = null
    var medicineCompanyName: String? = null
    var medicineName: String? = null
    var patientMedicineUid: String? = null
    var flag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientMedicineAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBarDialog = ProgressBarDialog(this)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        patientUid = intent.getStringExtra("patientUid")




        if (intent != null && intent.hasExtra("itemUpdate")) {  // data update key access this class

            flag = 1

            patientMedicineUid = intent.getStringExtra("patientMedicineUid")   // key set  variable

            Log.e("TAG", "patientMedicineUid: " + patientMedicineUid)


            mDbRef.child("PatientList").child(patientUid!!).child("PatientMedicine")
                .child(patientMedicineUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val medicineItem =
                                snapshot.getValue(PatientMedicineModelClass::class.java)
                            if (medicineItem != null) {
                                // User data retrieved successfully
                                val patientMedicineUid = medicineItem.patientMedicineUid
                                frontImage = medicineItem.frontImage
                                backImage = medicineItem.backImage
                                 medicineCompanyName = medicineItem.medicineCompanyName
                                 medicineName = medicineItem.medicineName
                                val medicineUnit = medicineItem.medicineUnit
                                val medicineMorningTime = medicineItem.medicineMorningTime
                                val medicineAfternoonTime = medicineItem.medicineAfternoonTime
                                val medicineNightTime = medicineItem.medicineNightTime




                                Log.e("TAG", "frontImage:  $frontImage ")
                                Log.e("TAG", "medicineName:  $medicineName ")


                                var adapter = ViewPagerAdapter(this@PatientMedicineAddActivity)
                                binding.viewPager.adapter = adapter
                                binding.wormDotsIndicator.attachTo(binding.viewPager)
                                adapter.notifyDataSetChanged()


                                var imageList = ArrayList<String>()
                                imageList.add(frontImage.toString())
                                imageList.add(backImage.toString())

                                adapter.updateList(imageList)
                                binding.txtMedicineCompanyName.text = medicineCompanyName.toString()
                                binding.txtMedicineName.text = medicineName.toString()
                                binding.edtMedicineUnit.setText(medicineUnit.toString())
                                binding.edtMedicineMorning.setText(medicineMorningTime.toString())
                                binding.edtMedicineAfternoon.setText(medicineAfternoonTime.toString())
                                binding.edtMedicineNight.setText(medicineNightTime.toString())

                                //change page title
                                binding.txtTitle.text = "Medicine Edit"
                                //change button name
                                binding.txtSave.text = "Update"
                                progressBarDialog.dismiss()
                            }
                        } else {
                            // User data does not exist
                            Toast.makeText(
                                this@PatientMedicineAddActivity,
                                "No Medicine",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
        else {
            var medicineUid = intent.getStringExtra("medicineUid")
            Log.e("TAG", "medicineUid:  $medicineUid ")
            var adapter = ViewPagerAdapter(this)
            binding.viewPager.adapter = adapter
            binding.wormDotsIndicator.attachTo(binding.viewPager)
            adapter.notifyDataSetChanged()

            progressBarDialog.show()
            mDbRef.child("MedicineList").child(medicineUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val medicineItem = snapshot.getValue(MedicineModelClass::class.java)
                            if (medicineItem != null) {
                                // User data retrieved successfully
                                val medicineUid = medicineItem.medicineUid
                                frontImage = medicineItem.frontImage
                                backImage = medicineItem.backImage
                                medicineCompanyName = medicineItem.medicineCompanyName
                                medicineName = medicineItem.medicineName
                                var medicineUse = medicineItem.medicineUse

                                // Handle the user data as needed
                                var imageList = ArrayList<String>()


                                Log.e("TAG", "frontImage:  $frontImage ")
                                Log.e("TAG", "medicineName:  $medicineName ")

                                imageList.add(frontImage.toString())
                                imageList.add(backImage.toString())

                                binding.txtMedicineCompanyName.text =
                                    medicineCompanyName.toString()
                                binding.txtMedicineName.text = medicineName.toString()

                                progressBarDialog.dismiss()
                                adapter.updateList(imageList)
                            }
                        } else {
                            // User data does not exist
                            Toast.makeText(
                                this@PatientMedicineAddActivity,
                                "No Medicine",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            progressBarDialog.dismiss()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
        binding.txtSave.setOnClickListener {

            var medicineUnit = binding.edtMedicineUnit.text.toString()
            var medicineMorningTime = binding.edtMedicineMorning.text.toString()
            var medicineAfternoonTime = binding.edtMedicineAfternoon.text.toString()
            var medicineNightTime = binding.edtMedicineNight.text.toString()


            if (medicineUnit.isEmpty()) {
                Toast.makeText(this, "Medicine Unit is empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (medicineMorningTime.isEmpty() || medicineAfternoonTime.isEmpty() || medicineNightTime.isEmpty()) {
                Toast.makeText(this, "Medicine Time is empty", Toast.LENGTH_SHORT).show()
            } else {
                progressBarDialog.show()
                if (flag == 1) {

                    mDbRef.child("PatientList").child(patientUid!!).child("PatientMedicine")
                        .child(patientMedicineUid!!).setValue(
                            PatientMedicineModelClass(
                                frontImage!!,
                                backImage!!,
                                medicineCompanyName!!,
                                medicineName!!,
                                medicineUnit,
                                medicineMorningTime,
                                medicineAfternoonTime,
                                medicineNightTime,
                                patientMedicineUid!!
                            )
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Record Save Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            var i = Intent(this, PatientMedicineActivity::class.java)
                            i.putExtra("patientUid",patientUid)
                            startActivity(i)
                            finish()
                            progressBarDialog.dismiss()
                        }
                    }.addOnFailureListener {
                        Log.e("TAG", "fail: " + it.message)
                        progressBarDialog.dismiss()

                    }
                } else {
                    var patientMedicineUid = UUID.randomUUID().toString()

                    mDbRef.child("PatientList").child(patientUid!!).child("PatientMedicine")
                        .child(patientMedicineUid).setValue(
                        PatientMedicineModelClass(
                            frontImage!!,
                            backImage!!,
                            medicineCompanyName!!,
                            medicineName!!,
                            medicineUnit,
                            medicineMorningTime,
                            medicineAfternoonTime,
                            medicineNightTime,
                            patientMedicineUid
                        )
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Record Save Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            var i = Intent(this, PatientMedicineActivity::class.java)
                            i.putExtra("patientUid",patientUid)
                            startActivity(i)
                            finish()
                            progressBarDialog.dismiss()
                        }
                    }.addOnFailureListener {
                        Log.e("TAG", "fail: " + it.message)
                        progressBarDialog.dismiss()
                    }
                }
            }
        }
    }
}