package com.sdsoft.drmdmedicine.Admin_panel.Patient_data.medicine

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
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
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.ViewPagerAdapter
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.databinding.ActivityPatientMedicineViewBinding
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding

class PatientMedicineViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityPatientMedicineViewBinding
    lateinit var progressBarDialog: ProgressBarDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference
    var patientUid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPatientMedicineViewBinding.inflate(layoutInflater)
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
        var patientMedicineUid = intent.getStringExtra("patientMedicineUid")
         patientUid = intent.getStringExtra("patientUid")

        Log.e("TAG", "patientMedicineUid:  $patientMedicineUid ")
        var adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.wormDotsIndicator.attachTo(binding.viewPager)
        adapter.notifyDataSetChanged()

        progressBarDialog.show()
        mDbRef.child("PatientList").child(patientUid!!).child("PatientMedicine").child(patientMedicineUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val medicineItem = snapshot.getValue(PatientMedicineModelClass::class.java)
                        if (medicineItem != null) {
                            // User data retrieved successfully
                            val patientMedicineUid = medicineItem.patientMedicineUid
                            val frontImage = medicineItem.frontImage
                            val backImage = medicineItem.backImage
                            val medicineCompanyName = medicineItem.medicineCompanyName
                            val medicineName = medicineItem.medicineName
                            val medicineUnit = medicineItem.medicineUnit
                            val medicineMorningTime = medicineItem.medicineMorningTime
                            val medicineAfternoonTime = medicineItem.medicineAfternoonTime
                            val medicineNightTime = medicineItem.medicineNightTime

                            // Handle the user data as needed
                            var imageList = ArrayList<String>()


                            Log.e("TAG", "frontImage:  $frontImage ")
                            Log.e("TAG", "medicineName:  $medicineName ")

                            imageList.add(frontImage.toString())
                            imageList.add(backImage.toString())

                            binding.txtMedicineCompanyName.text =
                                medicineCompanyName.toString()
                            binding.txtMedicineName.text = medicineName.toString()
                            binding.txtMedicineUnit.text = medicineUnit.toString()
                            binding.txtMedicineMorning.text = medicineMorningTime.toString()
                            binding.txtMedicineAfternoon.text = medicineAfternoonTime.toString()
                            binding.txtMedicineNight.text = medicineNightTime.toString()
                            progressBarDialog.dismiss()
                            adapter.updateList(imageList)
                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(this@PatientMedicineViewActivity, "No Medicine", Toast.LENGTH_SHORT)
                            .show()
                        progressBarDialog.dismiss()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        binding.txtEdit.setOnClickListener {
//            var i = Intent(this, PatientMedicineAddActivity::class.java)
//            i.putExtra("patientMedicineUid", patientMedicineUid)
//            i.putExtra("patientUid", patientUid)
//            i.putExtra("itemUpdate", true)
//            startActivity(i)
        }

        binding.cdDelete.setOnClickListener {
            deleteRecordFromDatabase(patientMedicineUid)
        }
    }

    private fun deleteRecordFromDatabase(patientMedicineUid: String) {

        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("PatientList").child(patientUid!!).child("PatientMedicine").child(patientMedicineUid).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onBackPressed()
                        Toast.makeText(this, "Record Deleted Successfully", Toast.LENGTH_SHORT)
                            .show()

                    }
                }.addOnFailureListener {
                    Log.e("TAG", "initView: " + it.message)
                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT)
                        .show()
                }

            deleteDialog.dismiss()
        }

        deleteDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

    }
}