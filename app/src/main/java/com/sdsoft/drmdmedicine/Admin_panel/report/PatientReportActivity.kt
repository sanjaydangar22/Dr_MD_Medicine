package com.sdsoft.drmdmedicine.Admin_panel.report

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
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
import com.sdsoft.drmdmedicine.Admin_panel.activity.MedicineViewActivity
import com.sdsoft.drmdmedicine.Admin_panel.activity.PatientDataViewActivity
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.MedicineListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientDataViewBinding
import com.sdsoft.drmdmedicine.databinding.ActivityPatientReportBinding

class PatientReportActivity : AppCompatActivity() {
    lateinit var patientDataViewBinding: ActivityPatientReportBinding

    lateinit var progressBarDialog: ProgressBarDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    lateinit var adapter: ReportAdapter
    var reportList = ArrayList<ReportModelClass>()

    lateinit var patientUid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientDataViewBinding = ActivityPatientReportBinding.inflate(layoutInflater)
        setContentView(patientDataViewBinding.root)
        // Inflate the layout for this fragment
        progressBarDialog = ProgressBarDialog(this)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        patientUid = intent.getStringExtra("patientUid").toString()
        Log.e("TAG", "patientUid:  $patientUid ")
        initView()
        reports()
    }


    private fun initView() {

        patientDataViewBinding.imgBack.setOnClickListener {

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

                            Glide.with(this@PatientReportActivity).load(patientImage)
                                .placeholder(R.drawable.ic_image)
                                .into(patientDataViewBinding.imgPatientImage)
                            patientDataViewBinding.txtPatientName.text = patientName.toString()

                            progressBarDialog.dismiss()
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

//        add new report
        patientDataViewBinding.imgAddReport.setOnClickListener {
            var i = Intent(this, AddReportActivity::class.java)
            i.putExtra("patientUid", patientUid)
            startActivity(i)
            finish()
        }
    }

    private fun reports() {

//        Reports List adapter
        adapter = ReportAdapter(this) {

            var i = Intent(this, ReportViewActivity::class.java)
            i.putExtra("patientUid", patientUid)
            i.putExtra("reportUid", it.reportUid)
            startActivity(i)
        }
        var manger = GridLayoutManager(this, 2)

        patientDataViewBinding.rcvReportList.layoutManager = manger
        patientDataViewBinding.rcvReportList.adapter = adapter

        //progress dialog show
        progressBarDialog.show()


        //       Patient Report  show in recycler view
        mDbRef.child("PatientList").child(patientUid).child("Reports")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reportList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(ReportModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange: " + data?.reportName
                        )
                        data?.let { it1 -> reportList.add(it1) }
                    }

                    if (reportList.isEmpty()) {
                        patientDataViewBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (reportList.isNotEmpty()) {
                        patientDataViewBinding.linNoDataFound.visibility = View.GONE
                    }

                    progressBarDialog.dismiss()
                    adapter.updateList(reportList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }
}