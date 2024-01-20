package com.sdsoft.drmdmedicine.Admin_panel.Patient_data_view.medicine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.sdsoft.drmdmedicine.Admin_panel.activity.AddMedicineActivity
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.ViewPagerAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientMedicineAddBinding

class PatientMedicineAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityPatientMedicineAddBinding
    lateinit var progressBarDialog: ProgressBarDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference
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
                            val frontImage = medicineItem.frontImage
                            val backImage = medicineItem.backImage
                            val medicineCompanyName = medicineItem.medicineCompanyName
                            val medicineName = medicineItem.medicineName
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

        binding.txtSave.setOnClickListener {

        }
    }
}