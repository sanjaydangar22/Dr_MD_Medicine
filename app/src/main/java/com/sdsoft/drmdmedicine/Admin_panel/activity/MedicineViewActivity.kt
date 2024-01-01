package com.sdsoft.drmdmedicine.Admin_panel.activity

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
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.databinding.ActivityMedicineViewBinding
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding

class MedicineViewActivity : AppCompatActivity() {

    lateinit var medicineViewBinding: ActivityMedicineViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        medicineViewBinding = ActivityMedicineViewBinding.inflate(layoutInflater)
        setContentView(medicineViewBinding.root)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {

        medicineViewBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
        var medicineUid = intent.getStringExtra("medicineUid")

        Log.e("TAG", "medicineUid:  $medicineUid ")
        var adapter = ViewPagerAdapter(this)
        medicineViewBinding.viewPager.adapter = adapter
        medicineViewBinding.wormDotsIndicator.attachTo(medicineViewBinding.viewPager)
        adapter.notifyDataSetChanged()

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

                            medicineViewBinding.txtMedicineCompanyName.text =
                                medicineCompanyName.toString()
                            medicineViewBinding.txtMedicineName.text = medicineName.toString()
                            medicineViewBinding.txtMedicineUse.text = medicineUse.toString()

                            adapter.updateList(imageList)
                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(this@MedicineViewActivity, "No Medicine", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        medicineViewBinding.txtEdit.setOnClickListener {
            var i = Intent(this, AddMedicineActivity::class.java)
            i.putExtra("medicineUid", medicineUid)
            i.putExtra("itemUpdate", true)
            startActivity(i)
        }

        medicineViewBinding.cdDelete.setOnClickListener {
            deleteRecordFromDatabase(medicineUid)
        }
    }

    private fun deleteRecordFromDatabase(medicineUid: String) {

        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("MedicineList").child(medicineUid).removeValue()
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