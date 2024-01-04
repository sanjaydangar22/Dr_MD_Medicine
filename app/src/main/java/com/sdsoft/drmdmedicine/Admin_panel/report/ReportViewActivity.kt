package com.sdsoft.drmdmedicine.Admin_panel.report

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
import com.sdsoft.drmdmedicine.Admin_panel.activity.AddMedicineActivity
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.ViewPagerAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityMedicineViewBinding
import com.sdsoft.drmdmedicine.databinding.ActivityReportViewBinding
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding

class ReportViewActivity : AppCompatActivity() {

    lateinit var reportViewBinding: ActivityReportViewBinding
    lateinit var progressBarDialog: ProgressBarDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportViewBinding = ActivityReportViewBinding.inflate(layoutInflater)
        setContentView(reportViewBinding.root)

        progressBarDialog = ProgressBarDialog(this)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {

        reportViewBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
        var patientUid = intent.getStringExtra("patientUid").toString()
        var reportUid = intent.getStringExtra("reportUid").toString()

        Log.e("TAG", "reportUid:  $reportUid ")


        progressBarDialog.show()
        mDbRef.child("PatientList").child(patientUid).child("Reports").child(reportUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val reportItem = snapshot.getValue(ReportModelClass::class.java)
                        if (reportItem != null) {
                            // User data retrieved successfully
                            val reportUid = reportItem.reportUid
                            val reportImage = reportItem.reportImage
                            val reportName = reportItem.reportName




                            reportViewBinding.txtReportName.text =
                                reportName.toString()

                            Glide.with(this@ReportViewActivity).load(reportImage)
                                .placeholder(R.drawable.ic_image)
                                .into(reportViewBinding.imgReportImage)

                            progressBarDialog.dismiss()

                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(this@ReportViewActivity, "No Reports", Toast.LENGTH_SHORT)
                            .show()
                        progressBarDialog.dismiss()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })



        reportViewBinding.cdDelete.setOnClickListener {
            deleteRecordFromDatabase(patientUid, reportUid)
        }
    }

    private fun deleteRecordFromDatabase(patientUid: String, reportUid: String) {

        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("PatientList").child(patientUid).child("Reports").child(reportUid)
                .removeValue()
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