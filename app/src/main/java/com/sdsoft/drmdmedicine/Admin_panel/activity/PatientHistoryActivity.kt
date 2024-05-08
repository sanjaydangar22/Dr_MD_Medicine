package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.DatePickerDialog
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
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientHistoryBinding
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PatientHistoryActivity : BaseActivity(R.layout.activity_patient_history) {
    lateinit var binding: ActivityPatientHistoryBinding
    var patientList = ArrayList<PatientModelClass>()
    private var selectedDate: String? = null
    lateinit var adapter: AddAppointmentsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().reference
        progressBarDialog = ProgressBarDialog(this)

        currentDateAndSelectDateGet()
        initView()
    }

    private fun currentDateAndSelectDateGet() {
        // Set up date format
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Set current date to the TextView and selectedDate variable
        val currentDate = Calendar.getInstance()
        val formattedCurrentDate = dateFormat.format(currentDate.time)
        binding.txtDate.text = formattedCurrentDate
        selectedDate = formattedCurrentDate

        // Set OnClickListener on the TextView to open DatePickerDialog
        binding.txtDate.setOnClickListener {

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the selected date
                    val selectedDateCalendar = Calendar.getInstance()
                    selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay)

                    val formattedDate = dateFormat.format(selectedDateCalendar.time)

                    progressBarDialog.show()

                    // Set the formatted date in the TextView
                    binding.txtDate.text = formattedDate
                    selectedDate = formattedDate
                    adapter.notifyDataSetChanged()
                    initView()
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

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


        //        patient List adapter

        adapter = AddAppointmentsListAdapter(this, "PatientHistoryList", {
            var i = Intent(this, PatientCheckUpActivity::class.java)
            i.putExtra("patientUid", it.patientUid)
            startActivity(i)
            finish()
        }, {       //delete data
            deleteRecordFromDatabase(it.patientUid!!)
        })
        var manger = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rcvPatientList.layoutManager = manger
        binding.rcvPatientList.adapter = adapter
        var appointmentList = ArrayList<PatientModelClass>()
        //       patient list show in recycler view
        mDbRef.child("PatientHistoryData").child(selectedDate!!)
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
            mDbRef.child("PatientHistoryData").child(selectedDate!!).child(patientUid)
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
        mDbRef.child("PatientHistoryData").child(selectedDate!!).orderByChild("patientName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<PatientModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(PatientModelClass::class.java)
                        item?.let { searchItems.add(it) }
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