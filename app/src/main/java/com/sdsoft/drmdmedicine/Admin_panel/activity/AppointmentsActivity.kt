package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsoft.drmdmedicine.Admin_panel.Patient_data.AddPatientActivity
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.AddAppointmentsListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityAppointmentsBinding
import com.sdsoft.drmdmedicine.databinding.DialogAddNewAppointmentsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppointmentsActivity : BaseActivity(R.layout.activity_appointments) {
    lateinit var binding: ActivityAppointmentsBinding
    lateinit var dialog: Dialog
    lateinit var dialogBinding: DialogAddNewAppointmentsBinding
    lateinit var adapter: AddAppointmentsListAdapter

    var appointmentsNumber: Int = 0
    var listType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().reference
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

        binding.imgAddAppointment.setOnClickListener {
            addNewAppointmentDialog()
        }

        //        Appointment List adapter
        listType = "AppointmentList"
        adapter = AddAppointmentsListAdapter(this, listType!!) {
            var i = Intent(this, PatientCheckUpActivity::class.java)
            i.putExtra("patientUid", it.patientUid)
            startActivity(i)
            finish()
        }
        var manger = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rcvAppointmentsList.layoutManager = manger
        binding.rcvAppointmentsList.adapter = adapter
        var appointmentList = ArrayList<PatientModelClass>()
        //       AppointmentList show in recycler view
        mDbRef.child("AppointmentList")
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
                        println("No appointments found in the specified range.")
                    }

                    adapter.updateList(appointmentList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }

    //    search view function
    private fun searchAppointmentItems(query: String) {
        mDbRef.child("AppointmentList").orderByChild("patientName")
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

    private fun addNewAppointmentDialog() {
        dialog = Dialog(this)
        dialogBinding = DialogAddNewAppointmentsBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Get the current date
        val currentDate = Calendar.getInstance()
        // Format and set the current date in the first TextView
        val formattedCurrentDate = dateFormat.format(currentDate.time)

        searchPatientFun()
        patientListShowFun(formattedCurrentDate)

        dialogBinding.imgClose.setOnClickListener {
            dialog.dismiss()

        }

        dialogBinding.imgAddNewPatient.setOnClickListener {
            // Increase appointmentsNumber by 1 when adding a new appointment
            appointmentsNumber += 1
            var i = Intent(this, AddPatientActivity::class.java)
            i.putExtra("appointmentsNumber", appointmentsNumber)
            i.putExtra("timestamp", formattedCurrentDate)
            i.putExtra("addNewAppointmentWithPatient", true)
            startActivity(i)
            finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun searchPatientFun() {
        //search patient
        dialogBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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


    private fun patientListShowFun(formattedCurrentDate: String) {

        //        patient List adapter
        listType = "patientList"
        adapter = AddAppointmentsListAdapter(this, listType!!) {
            // Increase appointmentsNumber by 1 when adding a new appointment
            appointmentsNumber += 1
            //add new Appointment data
            var i = Intent(this, AddPatientActivity::class.java)
            i.putExtra("patientUid", it.patientUid)
            i.putExtra("appointmentsNumber", appointmentsNumber)
            i.putExtra("timestamp", formattedCurrentDate)
            i.putExtra("addNewAppointment", true)
            startActivity(i)
            finish()
        }
        var manger = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        dialogBinding.rcvPatientList.layoutManager = manger
        dialogBinding.rcvPatientList.adapter = adapter

        var patientList = ArrayList<PatientModelClass>()
        //        patient list show in recycler view
        mDbRef.child("PatientList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    patientList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(PatientModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange: " + data?.patientName + data?.patientAge
                        )
                        data?.let { it1 -> patientList.add(it1) }
                    }

                    if (patientList.isEmpty()) {
                        dialogBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (patientList.isNotEmpty()) {
                        dialogBinding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(patientList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


    //    search view function
    private fun searchItems(query: String) {
        mDbRef.child("PatientList").orderByChild("patientName")
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
                        dialogBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (searchItems.isNotEmpty()) {
                        dialogBinding.linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }
}