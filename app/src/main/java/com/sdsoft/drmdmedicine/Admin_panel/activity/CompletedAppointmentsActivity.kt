package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.AddAppointmentsListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityCompletedAppointmentsBinding
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding

class CompletedAppointmentsActivity : BaseActivity(R.layout.activity_completed_appointments) {
    lateinit var binding: ActivityCompletedAppointmentsBinding
    lateinit var dialog: Dialog
    lateinit var adapter: AddAppointmentsListAdapter
    var userType: String? = null
    var appointmentsNumber: Int = 0
    var listType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().reference
        userType = intent.getStringExtra("userType")
        initView()
    }

    private fun initView() {


        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        //search patient
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchAppointmentItems(newText)
                }
                return true
            }
        })


        //        Appointment List adapter
        listType = "AppointmentCompletedList"
        adapter = AddAppointmentsListAdapter(this, listType!!, {
            if (userType == "Doctor") {
                var i = Intent(this, PatientCheckUpActivity::class.java)
                i.putExtra("patientUid", it.patientUid)
                startActivity(i)
                finish()
            }
        },{
            //delete data
            deleteRecordFromDatabase(it.patientUid!!)
        })
        var manger = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rcvAppointmentsList.layoutManager = manger
        binding.rcvAppointmentsList.adapter = adapter
        var appointmentList = ArrayList<PatientModelClass>()
        //       AppointmentList show in recycler view
        mDbRef.child("AppointmentCompletedList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentList.clear()

                    for (i in snapshot.children) {
                        var data = i.getValue(PatientModelClass::class.java)

                        data?.let { it1 -> appointmentList.add(it1) }
                    }

                    if (appointmentList.isEmpty()) {
                        binding.linNoDataFound.visibility = View.VISIBLE
                    } else if (appointmentList.isNotEmpty()) {
                        binding.linNoDataFound.visibility = View.GONE
                    }
                    val sortedList = appointmentList.sortedBy { it.appointmentsNumber }
                    val filteredList =
                        sortedList.filter { it.appointmentsNumber in 1..appointmentList.size }
                    // Check if the filteredList is not empty
                    if (filteredList.isNotEmpty()) {
                        // Find the last element based on appointmentsNumber
                        val lastAppointment = filteredList.lastOrNull()

                        // Now you can use the lastAppointment as needed
                        appointmentsNumber = lastAppointment!!.appointmentsNumber

                    } else {

                    }

                    adapter.updateList(appointmentList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }

    private fun deleteRecordFromDatabase(patientUid: String) {

        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("AppointmentCompletedList").child(patientUid)
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

    //    search view function
    private fun searchAppointmentItems(query: String) {
        mDbRef.child("AppointmentCompletedList").orderByChild("patientName")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<PatientModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(PatientModelClass::class.java)
                        if (item!!.patientName!!.lowercase().contains(query.lowercase())) {
                            searchItems.add(item)
                        }
                    }


                    adapter.updateList(searchItems)

                    if (searchItems.isEmpty()) {
                        binding.linNoDataFound.visibility = View.VISIBLE
                    } else if (searchItems.isNotEmpty()) {
                        binding.linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }

}