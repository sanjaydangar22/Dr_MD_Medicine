package com.sdsoft.drmdmedicine.Admin_panel.Patient_data_view.medicine

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.MedicineListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientMedicineBinding

class PatientMedicineActivity : AppCompatActivity() {

    lateinit var binding: ActivityPatientMedicineBinding

    lateinit var progressBarDialog: ProgressBarDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    lateinit var adapter: MedicineListAdapter
    var patientMedicineList = ArrayList<PatientMedicineModelClass>()
    var medicineList = ArrayList<MedicineModelClass>()
    var patientUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBarDialog = ProgressBarDialog(this)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        patientUid = intent.getStringExtra("patientUid").toString()
        Log.e("TAG", "patientUid:  $patientUid ")

        initView()
        patientMedicineListFunction()


    }


    private fun initView() {

        binding.imgBack.setOnClickListener {

            onBackPressed()
        }


        progressBarDialog.show()

        mDbRef.child("PatientList").child(patientUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val patientItem = snapshot.getValue(PatientModelClass::class.java)
                        if (patientItem != null) {
                            // User data retrieved successfully
                            patientUid = patientItem.patientUid!!
                            val patientImage = patientItem.patientImage
                            val patientName = patientItem.patientName


                            Log.e("TAG", "patientImage:  $patientImage ")

                            Glide.with(this@PatientMedicineActivity).load(patientImage)
                                .placeholder(R.drawable.ic_image)
                                .into(binding.imgPatientImage)
                            binding.txtPatientName.text = patientName.toString()

                            progressBarDialog.dismiss()
                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(
                            this@PatientMedicineActivity,
                            "Data not Found",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


//        add new medicine
        binding.imgAddMedicine.setOnClickListener {
            medicineDialog()
        }
    }

    private fun patientMedicineListFunction() {
        var adapter = PatientMedicineListAdapter(this@PatientMedicineActivity) {

            var i = Intent(this, PatientMedicineViewActivity::class.java)
            i.putExtra("patientUid", patientUid)
            i.putExtra("patientMedicineUid", it.patientMedicineUid)
            startActivity(i)
        }
        var manger = GridLayoutManager(this@PatientMedicineActivity, 2)

        binding.rcvMedicineList.layoutManager = manger
        binding.rcvMedicineList.adapter = adapter
        mDbRef.child("PatientList").child(patientUid!!).child("PatientMedicine")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    patientMedicineList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(PatientMedicineModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange: " + data?.medicineName + data?.medicineCompanyName
                        )
                        data?.let { it1 -> patientMedicineList.add(it1) }
                    }

                    if (patientMedicineList.isEmpty()) {
                        binding.linNoDataFound.visibility = View.VISIBLE
                    } else if (patientMedicineList.isNotEmpty()) {
                        binding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(patientMedicineList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun medicineDialog() {

        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_patient_medicine)

        var searchView: SearchView = dialog.findViewById(R.id.searchView)
        var recyclerView: RecyclerView = dialog.findViewById(R.id.rcvMedicineList)
//search medicine

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchItems(newText, dialog)
                }
                return true
            }
        })


        adapter = MedicineListAdapter(this@PatientMedicineActivity) {

            var i = Intent(this, PatientMedicineAddActivity::class.java)
            i.putExtra("patientUid", patientUid)
            i.putExtra("medicineUid", it.medicineUid)
            startActivity(i)
        }
        var manger = GridLayoutManager(this@PatientMedicineActivity, 2)

        recyclerView.layoutManager = manger
        recyclerView.adapter = adapter

        adapterClass()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

    }

    //    search view function
    private fun searchItems(query: String, dialog: Dialog) {
        mDbRef.child("MedicineList").orderByChild("medicineName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<MedicineModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(MedicineModelClass::class.java)
                        item?.let { searchItems.add(it) }
                    }


                    adapter.updateList(searchItems)
                    var linNoDataFound: LinearLayout = dialog.findViewById(R.id.linNoDataFound)
                    if (searchItems.isEmpty()) {
                        linNoDataFound.visibility = View.VISIBLE
                    } else if (searchItems.isNotEmpty()) {
                        linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }

    private fun adapterClass() {
//        adapter = MedicineListAdapter(this@PatientMedicineActivity) {
//
//            var i = Intent(this, MedicineViewActivity::class.java)
//            i.putExtra("medicineUid", it.medicineUid)
//            startActivity(i)
//        }
//        var manger = GridLayoutManager(this@PatientMedicineActivity,2)
//
//        binding.rcvMedicineList.layoutManager = manger
//        binding.rcvMedicineList.adapter = adapter

//        medicine list show in recycler view
        mDbRef.child("MedicineList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicineList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(MedicineModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange: " + data?.medicineName + data?.medicineCompanyName
                        )
                        data?.let { it1 -> medicineList.add(it1) }
                    }

                    if (medicineList.isEmpty()) {
                        binding.linNoDataFound.visibility = View.VISIBLE
                    } else if (medicineList.isNotEmpty()) {
                        binding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(medicineList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

}