package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
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
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ReportModelClass
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.PatientCheckUpAdapter
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.PatientCheckUpDetailsAdapter
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.ReportAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientCheckUpDetails
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientDataViewBinding
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding

class PatientDataViewActivity : BaseActivity(R.layout.activity_patient_data_view) {

    lateinit var patientDataViewBinding: ActivityPatientDataViewBinding
    private lateinit var auth: FirebaseAuth
    lateinit var storageReference: StorageReference
    lateinit var patientUid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        patientDataViewBinding = ActivityPatientDataViewBinding.inflate(layoutInflater)
        setContentView(patientDataViewBinding.root)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        patientUid = intent.getStringExtra("patientUid").toString()
        Log.e("TAG", "patientUid:  $patientUid ")
        initView()
    }

    private fun initView() {

        patientDataViewBinding.imgBack.setOnClickListener {
            onBackPressed()
        }



        mDbRef.child("PatientList").child(patientUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val patientItem = snapshot.getValue(PatientModelClass::class.java)
                        if (patientItem != null) {
                            // User data retrieved successfully
                            var patientUid = patientItem.patientUid
                            val patientImage = patientItem.patientImage
                            val patientName = patientItem.patientName
                            var patientMobileNo = patientItem.patientMobileNo
                            var patientVillage = patientItem.patientVillage
                            var patientAge = patientItem.patientAge
                            var patientWeight = patientItem.patientWeight
                            var patientGender = patientItem.patientGender


                            if (!isDestroyed) {
                                Glide.with(this@PatientDataViewActivity).load(patientImage)
                                    .placeholder(R.drawable.ic_image)
                                    .into(patientDataViewBinding.imgPatientImage)
                            }
                            patientDataViewBinding.txtPatientName.text = patientName.toString()
                            patientDataViewBinding.txtPatientMobileNo.text =
                                patientMobileNo.toString()
                            patientDataViewBinding.txtPatientVillage.text =
                                patientVillage.toString()
                            patientDataViewBinding.txtPatientAge.text = patientAge.toString()
                            patientDataViewBinding.txtPatientWeight.text = patientWeight.toString()
                            patientDataViewBinding.txtPatientGender.text = patientGender.toString()


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


//date list show
        mDbRef.child("PatientList").child(patientUid).child("PatientCheckUpDetails")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val checkUpDetailsList = mutableListOf<PatientCheckUpDetails>()
                    // Iterate through the dataSnapshot to retrieve CheckUpDetails
                    for (data in snapshot.children) {
                        checkUpDetailsList.add(PatientCheckUpDetails(data.key!!))
                    }

                    // Initialize the adapter
                    val adapter =
                        PatientCheckUpDetailsAdapter(checkUpDetailsList) { patientCheckUpDetails ->

                            progressBarDialog.show()
                            patientCheckUpDetailsShowFun(patientCheckUpDetails)
                        }
                    // Set up RecyclerView
                    patientDataViewBinding.rcvCheckUpDateList.apply {
                        layoutManager = LinearLayoutManager(
                            this@PatientDataViewActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        this.adapter = adapter // Use "this.adapter" instead of "adapter"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                }
            })


    }

    private fun patientCheckUpDetailsShowFun(patientCheckUpDetails: PatientCheckUpDetails) {

        patientDataViewBinding.linNotSelectAnyDate.visibility=View.GONE
        patientDataViewBinding.scrollView.visibility=View.VISIBLE
        val diseaseList = ArrayList<ModelClass>()
        mDbRef.child("PatientList").child(patientUid)
            .child("PatientCheckUpDetails").child(patientCheckUpDetails.date!!)
            .child("PatientDisease")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diseaseList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(ModelClass::class.java)
                        data?.let { diseaseList.add(it) }
                    }
                    val diseaseAdapter = PatientCheckUpAdapter(this@PatientDataViewActivity) {
                        patientCheckUpDiseaseDeleteFun(it,patientCheckUpDetails.date!!)
                    }
                    patientDataViewBinding.rcvDiseaseList.layoutManager =
                        LinearLayoutManager(
                            this@PatientDataViewActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    patientDataViewBinding.rcvDiseaseList.adapter = diseaseAdapter
                    if (diseaseList.isEmpty()) {
                        patientDataViewBinding.linDiseaseNoDataFound.visibility = View.VISIBLE
                    } else {
                        patientDataViewBinding.linDiseaseNoDataFound.visibility = View.GONE
                    }
                    diseaseAdapter.updateList(diseaseList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@PatientDataViewActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })


        val medicineList = ArrayList<ModelClass>()
        mDbRef.child("PatientList").child(patientUid)
            .child("PatientCheckUpDetails").child(patientCheckUpDetails.date!!)
            .child("PatientMedicine")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicineList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(ModelClass::class.java)
                        data?.let { medicineList.add(it) }
                    }
                    val medicineAdapter = PatientCheckUpAdapter(this@PatientDataViewActivity) {
                        patientCheckUpMedicineDeleteFun(it,patientCheckUpDetails.date!!)
                    }
                    patientDataViewBinding.rcvMedicineList.layoutManager =
                        LinearLayoutManager(
                            this@PatientDataViewActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    patientDataViewBinding.rcvMedicineList.adapter = medicineAdapter
                    if (medicineList.isEmpty()) {
                        patientDataViewBinding.linMedicineNoDataFound.visibility = View.VISIBLE
                    } else {
                        patientDataViewBinding.linMedicineNoDataFound.visibility = View.GONE
                    }
                    medicineAdapter.updateList(medicineList)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@PatientDataViewActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })


        val reportList = ArrayList<ReportModelClass>()
        mDbRef.child("PatientList").child(patientUid)
            .child("PatientCheckUpDetails").child(patientCheckUpDetails.date!!)
            .child("PatientReportImage")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reportList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(ReportModelClass::class.java)
                        data?.let { reportList.add(it) }
                    }
                    val reportAdapter = ReportAdapter(this@PatientDataViewActivity) {
                        var i = Intent(this@PatientDataViewActivity, ReportViewActivity::class.java)
                        i.putExtra("patientUid", patientUid)
                        i.putExtra("reportUid", it.reportUid)
                        i.putExtra("currentDateToday", patientCheckUpDetails.date)
                        startActivity(i)
                    }
                    patientDataViewBinding.rcvReportImageList.layoutManager =
                        LinearLayoutManager(
                            this@PatientDataViewActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    patientDataViewBinding.rcvReportImageList.adapter = reportAdapter
                    if (reportList.isEmpty()) {
                        patientDataViewBinding.linReportNoDataFound.visibility = View.VISIBLE
                    } else {
                        patientDataViewBinding.linReportNoDataFound.visibility = View.GONE
                    }
                    reportAdapter.updateList(reportList)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@PatientDataViewActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        Handler().postDelayed({
            progressBarDialog.dismiss()
        }, 3000)

    }

    private fun patientCheckUpDiseaseDeleteFun(diseaseUid: String, date: String) {
        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("PatientList").child(patientUid)
                .child("PatientCheckUpDetails").child(date).child("PatientDisease")
                .child(diseaseUid)
                .removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Record Deleted Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        progressBarDialog.dismiss()

                    }
                }.addOnFailureListener {

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
    private fun patientCheckUpMedicineDeleteFun(diseaseUid: String, date: String) {
        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("PatientList").child(patientUid)
                .child("PatientCheckUpDetails").child(date).child("PatientMedicine")
                .child(diseaseUid)
                .removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Record Deleted Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        progressBarDialog.dismiss()

                    }
                }.addOnFailureListener {

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