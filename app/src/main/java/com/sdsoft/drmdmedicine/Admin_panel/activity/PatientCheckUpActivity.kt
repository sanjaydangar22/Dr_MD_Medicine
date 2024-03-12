package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.DiseaseListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.DiseaseModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientCheckUpBinding
import com.sdsoft.drmdmedicine.databinding.DialogAddNewItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PatientCheckUpActivity : BaseActivity(R.layout.activity_patient_check_up) {
    lateinit var binding: ActivityPatientCheckUpBinding
    var patientUid: String? = null
    private lateinit var auth: FirebaseAuth
    lateinit var storageReference: StorageReference
    lateinit var diseaseDialog: Dialog
    lateinit var diseaseDialogBinding: DialogAddNewItemBinding
    var currentDateToday: String? = null
    lateinit var adapter: DiseaseListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientCheckUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        patientUid = intent.getStringExtra("patientUid")

        dataGet()
        currentDateGet()
        addDisease()
    }

    private fun dataGet() {
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
                            var patientMobileNo = patientItem.patientMobileNo
                            var patientVillage = patientItem.patientVillage
                            var patientAge = patientItem.patientAge
                            var patientWeight = patientItem.patientWeight
                            var patientGender = patientItem.patientGender



                            Glide.with(this@PatientCheckUpActivity).load(patientImage)
                                .placeholder(R.drawable.ic_image)
                                .into(binding.imgPatientImage)
                            binding.txtPatientName.text = patientName.toString()
                            binding.txtPatientMobileNo.text = patientMobileNo.toString()
                            binding.txtPatientVillage.text = patientVillage.toString()
                            binding.txtPatientAge.text = patientAge.toString()
                            binding.txtPatientWeight.text = patientWeight.toString()
                            binding.txtPatientGender.text = patientGender.toString()


                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(
                            this@PatientCheckUpActivity,
                            "Data not Found",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        //       disease List adapter
//        adapter = DiseaseListAdapter(this) {
//
//
//        }
//        var manger = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//
//        binding.rcvDiseaseList.layoutManager = manger
//        binding.rcvDiseaseList.adapter = adapter
//        var diseaseList = ArrayList<DiseaseModelClass>()
//        //        diseaseList show in recycler view
//        mDbRef.child("PatientList").child("PatientCheckUpDetails").child(currentDateToday!!)
//            .child("PatientDisease")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    diseaseList.clear()
//                    for (i in snapshot.children) {
//                        var data = i.getValue(DiseaseModelClass::class.java)
//
//                        data?.let { it1 -> diseaseList.add(it1) }
//                    }
//
//                    if (diseaseList.isEmpty()) {
////                        binding.linNoDataFound.visibility = View.VISIBLE
//                    } else if (diseaseList.isNotEmpty()) {
////                        binding.linNoDataFound.visibility = View.GONE
//                    }
//                    adapter.updateList(diseaseList)
//                    progressBarDialog.dismiss()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })
    }

    private fun currentDateGet() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        // Get the current date
        val currentDate = Calendar.getInstance()

        // Format and set the current date in the first TextView
        val formattedCurrentDate = dateFormat.format(currentDate.time)
        binding.txtCurrentDate.text = formattedCurrentDate
        currentDateToday = formattedCurrentDate


    }

    private fun addDisease() {
        binding.cdAddDisease.setOnClickListener {
            addDiseaseDialog()
        }
    }

    private fun addDiseaseDialog() {
        diseaseDialog = Dialog(this)
        diseaseDialogBinding = DialogAddNewItemBinding.inflate(layoutInflater)
        diseaseDialog.setContentView(diseaseDialogBinding.root)

        diseaseDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        diseaseDialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        diseaseDialogBinding.txtDialogTitle.text = "Add New Disease"
        searchDiseaseFun()
        diseaseListShowFun()

        diseaseDialogBinding.imgClose.setOnClickListener {
            diseaseDialog.dismiss()

        }

//        diseaseDialogBinding.imgAddNewPatient.setOnClickListener {
//            // Increase appointmentsNumber by 1 when adding a new appointment
//
//            var i = Intent(this, AddPatientActivity::class.java)
//            i.putExtra("appointmentsNumber", appointmentsNumber)
//            i.putExtra("timestamp", formattedCurrentDate)
//            i.putExtra("addNewAppointmentWithPatient", true)
//            startActivity(i)
//            finish()
//        }
        diseaseDialog.setCancelable(false)
        diseaseDialog.show()
    }

    private fun searchDiseaseFun() {
        //search patient
        diseaseDialogBinding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchItems(newText)
                }
                return true
            }
        })
    }

    //    search view function
    private fun diseaseListShowFun() {

        //       disease List adapter
        adapter = DiseaseListAdapter(this) {
            addPatientDiseaseFun(it)

        }
        var manger = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        diseaseDialogBinding.rcvItemList.layoutManager = manger
        diseaseDialogBinding.rcvItemList.adapter = adapter
        var diseaseList = ArrayList<DiseaseModelClass>()
        //        diseaseList show in recycler view
        mDbRef.child("DiseaseList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diseaseList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(DiseaseModelClass::class.java)

                        data?.let { it1 -> diseaseList.add(it1) }
                    }

                    if (diseaseList.isEmpty()) {
                        diseaseDialogBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (diseaseList.isNotEmpty()) {
                        diseaseDialogBinding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(diseaseList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

//        diseaseDialogBinding.imgAddNewPatient.setOnClickListener {
//            dialogBinding.btnSubmit.text = "Submit"
//            dialogBinding.cdDelete.visibility = View.GONE
//            dialog.show()
//            dialogBinding.btnSubmit.setOnClickListener {
//                var diseaseName = dialogBinding.edtDiseaseName.text.toString()
//
//                if (diseaseName.isEmpty()) {
//                    Toast.makeText(this, "Please Enter Disease Name", Toast.LENGTH_SHORT).show()
//                } else {
//                    addDiseaseFun(diseaseName)
//
//                }
//            }
//
//        }
    }

    private fun addPatientDiseaseFun(model: DiseaseModelClass) {
        mDbRef.child("PatientList").child(patientUid!!).child("PatientCheckUpDetails")
            .child(currentDateToday!!)
            .child("PatientDisease")
            .child(model.diseaseUid!!).setValue(model)
    }

    private fun searchItems(query: String) {
        mDbRef.child("DiseaseList").orderByChild("diseaseName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<DiseaseModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(DiseaseModelClass::class.java)
                        item?.let { searchItems.add(it) }
                    }


                    adapter.updateList(searchItems)

                    if (searchItems.isEmpty()) {
                        diseaseDialogBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (searchItems.isNotEmpty()) {
                        diseaseDialogBinding.linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }

}