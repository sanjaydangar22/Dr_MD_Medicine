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
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.PatientDiseaseAdapter
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
    private lateinit var binding: ActivityPatientCheckUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var diseaseDialog: Dialog
    private lateinit var diseaseDialogBinding: DialogAddNewItemBinding
    private var patientUid: String? = null
    private var currentDateToday: String? = null
    private lateinit var adapter: DiseaseListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientCheckUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth
        // Get the Firebase storage reference
        storageReference = FirebaseStorage.getInstance().reference

        patientUid = intent.getStringExtra("patientUid")

        dataGet()
        currentDateGet()
        dialogFun()
        addDisease()
    }

    private fun dataGet() {
        FirebaseDatabase.getInstance().reference.child("PatientList").child(patientUid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val patientItem = snapshot.getValue(PatientModelClass::class.java)
                        patientItem?.let {
                            // User data retrieved successfully
                            val patientImage = it.patientImage
                            val patientName = it.patientName
                            val patientMobileNo = it.patientMobileNo
                            val patientVillage = it.patientVillage
                            val patientAge = it.patientAge
                            val patientWeight = it.patientWeight
                            val patientGender = it.patientGender

                            Glide.with(this@PatientCheckUpActivity).load(patientImage)
                                .placeholder(R.drawable.ic_image)
                                .into(binding.imgPatientImage)
                            binding.txtPatientName.text = patientName
                            binding.txtPatientMobileNo.text = patientMobileNo
                            binding.txtPatientVillage.text = patientVillage
                            binding.txtPatientAge.text = patientAge
                            binding.txtPatientWeight.text = patientWeight
                            binding.txtPatientGender.text = patientGender
                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(
                            this@PatientCheckUpActivity,
                            "Data not Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@PatientCheckUpActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun currentDateGet() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val formattedCurrentDate = dateFormat.format(currentDate.time)
        binding.txtCurrentDate.text = formattedCurrentDate
        currentDateToday = formattedCurrentDate
    }

    private fun dialogFun() {
        diseaseDialog = Dialog(this)
        diseaseDialogBinding = DialogAddNewItemBinding.inflate(layoutInflater)
        diseaseDialog.setContentView(diseaseDialogBinding.root)
        diseaseDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        diseaseDialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        diseaseDialogBinding.imgClose.setOnClickListener {
            diseaseDialog.dismiss()
        }

        diseaseDialog.setCancelable(false)
    }

    private fun addDisease() {
        binding.cdAddDisease.setOnClickListener {
            addDiseaseDialog()
        }

        val diseaseAdapter = PatientDiseaseAdapter(this)
        binding.rcvDiseaseList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvDiseaseList.adapter = diseaseAdapter
        val diseaseList = ArrayList<DiseaseModelClass>()

        FirebaseDatabase.getInstance().reference.child("PatientList").child(patientUid!!)
            .child("PatientCheckUpDetails").child(currentDateToday!!).child("PatientDisease")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diseaseList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(DiseaseModelClass::class.java)
                        data?.let { diseaseList.add(it) }
                    }

                    if (diseaseList.isEmpty()) {
                        binding.linDiseaseNoDataFound.visibility = View.VISIBLE
                    } else {
                        binding.linDiseaseNoDataFound.visibility = View.GONE
                    }
                    diseaseAdapter.updateList(diseaseList)
                    // progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@PatientCheckUpActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun addDiseaseDialog() {
        diseaseDialogBinding.txtDialogTitle.text = "Add New Disease"
        searchDiseaseFun()
        diseaseListShowFun()
        diseaseDialog.setCancelable(false)
        diseaseDialog.show()
    }

    private fun diseaseListShowFun() {
        adapter = DiseaseListAdapter(this) {
            addPatientDiseaseFun(it)
            diseaseDialog.dismiss()
        }
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        diseaseDialogBinding.rcvItemList.layoutManager = manager
        diseaseDialogBinding.rcvItemList.adapter = adapter
        val diseaseList = ArrayList<DiseaseModelClass>()

        FirebaseDatabase.getInstance().reference.child("DiseaseList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diseaseList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(DiseaseModelClass::class.java)
                        data?.let { diseaseList.add(it) }
                    }

                    if (diseaseList.isEmpty()) {
                        diseaseDialogBinding.linNoDataFound.visibility = View.VISIBLE
                    } else {
                        diseaseDialogBinding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(diseaseList)
                    // progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@PatientCheckUpActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun searchDiseaseFun() {
        diseaseDialogBinding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchItems(it) }
                return true
            }
        })
    }

    private fun addPatientDiseaseFun(model: DiseaseModelClass) {
        FirebaseDatabase.getInstance().reference.child("PatientList").child(patientUid!!)
            .child("PatientCheckUpDetails").child(currentDateToday!!)
            .child("PatientDisease").child(model.diseaseUid!!).setValue(model)
    }

    private fun searchItems(query: String) {
        FirebaseDatabase.getInstance().reference.child("DiseaseList").orderByChild("diseaseName")
            .startAt(query).endAt(query + "\uf8ff")
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
                    } else {
                        diseaseDialogBinding.linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@PatientCheckUpActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
