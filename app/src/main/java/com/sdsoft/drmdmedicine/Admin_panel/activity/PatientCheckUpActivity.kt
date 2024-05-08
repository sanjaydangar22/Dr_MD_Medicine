package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.ReportAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ReportModelClass
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.DiseaseListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.PatientCheckUpAdapter
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.PatientCheckUpMedicineAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientMedicineModel
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityPatientCheckUpBinding
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding
import com.sdsoft.drmdmedicine.databinding.DialogAddMedicineBinding
import com.sdsoft.drmdmedicine.databinding.DialogAddNewItemBinding
import com.sdsoft.drmdmedicine.databinding.DialogAddPatientReportImageBinding
import com.sdsoft.drmdmedicine.databinding.DialogAppointmentsSaveBinding
import com.sdsoft.drmdmedicine.databinding.DialogShowListAndAddNewItemBinding
import com.sdsoft.drmdmedicine.databinding.ImageSelctedDialogBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class PatientCheckUpActivity : BaseActivity(R.layout.activity_patient_check_up) {
    private lateinit var binding: ActivityPatientCheckUpBinding
    private lateinit var storageReference: StorageReference
    private lateinit var diseaseDialog: Dialog
    private lateinit var diseaseDialogBinding: DialogShowListAndAddNewItemBinding
    private var patientUid: String? = null
    private var currentDateToday: String? = null
    private lateinit var adapter: DiseaseListAdapter
    var imageUploadCompleted = 0
    var imageAndNameSaveCompleted = 0
    var reportImagePath: Uri? = null
    lateinit var reportImageDialog: Dialog
    lateinit var reportImageDialogBinding: DialogAddPatientReportImageBinding

    var reportImage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientCheckUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // Get the Firebase storage reference
        storageReference = FirebaseStorage.getInstance().reference

        patientUid = intent.getStringExtra("patientUid")

        dataGetAndEditFun()
        currentDateGet()
        dialogFun()
        diseaseListFun()
        medicineListFun()
        reportImageListFun()
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.cdSaveData.setOnClickListener {
            saveDataAndAppointmentsCompleted()
        }
    }


    private fun dataGetAndEditFun() {
        mDbRef.child("PatientList").child(patientUid!!)
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

                    binding.imgEditBtn.setOnClickListener {
//edit data
                        var i = Intent(this@PatientCheckUpActivity, AddPatientActivity::class.java)
                        i.putExtra("patientUid", patientUid)
                        i.putExtra("itemUpdatePatientCheckUp", true)
                        startActivity(i)
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
        diseaseDialogBinding = DialogShowListAndAddNewItemBinding.inflate(layoutInflater)
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

    private fun diseaseListFun() {
        binding.cdAddDisease.setOnClickListener {
            addDiseaseDialog()
        }


        val diseaseList = ArrayList<ModelClass>()
        mDbRef.child("PatientList").child(patientUid!!)
            .child("PatientCheckUpDetails").child(currentDateToday!!).child("PatientDisease")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diseaseList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(ModelClass::class.java)
                        data?.let { diseaseList.add(it) }
                    }
                    val diseaseAdapter = PatientCheckUpAdapter(this@PatientCheckUpActivity) {
                        patientCheckUpDiseaseDeleteFun(it)
                    }
                    binding.rcvDiseaseList.layoutManager =
                        LinearLayoutManager(
                            this@PatientCheckUpActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    binding.rcvDiseaseList.adapter = diseaseAdapter
                    if (diseaseList.isEmpty()) {
                        binding.linDiseaseNoDataFound.visibility = View.VISIBLE
                    } else {
                        binding.linDiseaseNoDataFound.visibility = View.GONE
                    }
                    diseaseAdapter.updateList(diseaseList)
                    progressBarDialog.dismiss()
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

    private fun patientCheckUpDiseaseDeleteFun(diseaseUid: String) {
        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("PatientList").child(patientUid!!)
                .child("PatientCheckUpDetails").child(currentDateToday!!).child("PatientDisease")
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

    private fun addDiseaseDialog() {
        diseaseDialogBinding.txtDialogTitle.text = "Add New Disease"
        searchDiseaseFun()
        diseaseListShowFun()
        diseaseDialogBinding.imgAddNewItem.setOnClickListener {
            addNewDiseaseFun()
        }

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
        val diseaseList = ArrayList<ModelClass>()

        mDbRef.child("DiseaseList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diseaseList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(ModelClass::class.java)
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

    private fun addNewDiseaseFun() {
        var dialog = Dialog(this)
        var dialogBinding = DialogAddNewItemBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialogBinding.txtDialogTitle.text = "Add Disease"
        dialogBinding.imgClose.setOnClickListener {
            dialog.dismiss()
            dialogBinding.edtName.setText("")
        }

        dialogBinding.btnSubmit.text = "Submit"
        dialogBinding.cdDelete.visibility = View.GONE
        dialog.show()
        dialogBinding.btnSubmit.setOnClickListener {
            var name = dialogBinding.edtName.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please Enter Disease Name", Toast.LENGTH_SHORT).show()
            } else {
                var uid = UUID.randomUUID().toString()
                progressBarDialog.show()
                mDbRef.child("DiseaseList").child(uid)
                    .setValue(ModelClass(name, uid))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Record Save Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            addPatientDiseaseFun(ModelClass(name, uid))
                            dialogBinding.edtName.setText("")
                            progressBarDialog.dismiss()
                            dialog.dismiss()
                            diseaseDialog.dismiss()

                        }
                    }.addOnFailureListener {
                        progressBarDialog.dismiss()
                        dialog.dismiss()

                    }

            }
        }
    }

    private fun searchDiseaseFun() {
        diseaseDialogBinding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchDiseaseItems(it) }
                return true
            }
        })
    }

    private fun addPatientDiseaseFun(model: ModelClass) {
        mDbRef.child("PatientList").child(patientUid!!)
            .child("PatientCheckUpDetails").child(currentDateToday!!)
            .child("PatientDisease").child(model.uid!!).setValue(model)
    }

    private fun searchDiseaseItems(query: String) {
        mDbRef.child("DiseaseList").orderByChild("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<ModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ModelClass::class.java)
                        item?.let {

                            searchItems.add(it)


                        }
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
                }
            })
    }


    private fun medicineListFun() {
        binding.cdAddMedicine.setOnClickListener {
            addMedicineDialog()
        }


        val medicineList = ArrayList<PatientMedicineModel>()

        mDbRef.child("PatientList").child(patientUid!!)
            .child("PatientCheckUpDetails").child(currentDateToday!!).child("PatientMedicine")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicineList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(PatientMedicineModel::class.java)
                        data?.let { medicineList.add(it) }
                    }
                    val medicineAdapter =
                        PatientCheckUpMedicineAdapter(this@PatientCheckUpActivity) {
                            patientCheckUpMedicineEditFun(it)
                        }
                    binding.rcvMedicineList.layoutManager =
                        LinearLayoutManager(
                            this@PatientCheckUpActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    binding.rcvMedicineList.adapter = medicineAdapter
                    if (medicineList.isEmpty()) {
                        binding.linMedicineNoDataFound.visibility = View.VISIBLE
                    } else {
                        binding.linMedicineNoDataFound.visibility = View.GONE
                    }
                    medicineAdapter.updateList(medicineList)

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

    private fun addMedicineDialog() {
        diseaseDialogBinding.txtDialogTitle.text = "Add New Medicine"
        searchMedicineFun()
        medicineListShowFun()
        diseaseDialogBinding.imgAddNewItem.setOnClickListener {
            addNewMedicineFun()
        }

        diseaseDialog.setCancelable(false)
        diseaseDialog.show()
    }

    private fun medicineListShowFun() {
        adapter = DiseaseListAdapter(this) {
            addPatientMedicineFun(it)
            diseaseDialog.dismiss()
        }
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        diseaseDialogBinding.rcvItemList.layoutManager = manager
        diseaseDialogBinding.rcvItemList.adapter = adapter
        val medicineList = ArrayList<ModelClass>()

        mDbRef.child("MedicineList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicineList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(ModelClass::class.java)
                        data?.let { medicineList.add(it) }
                    }

                    if (medicineList.isEmpty()) {
                        diseaseDialogBinding.linNoDataFound.visibility = View.VISIBLE
                    } else {
                        diseaseDialogBinding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(medicineList)

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

    private fun addNewMedicineFun() {
        var dialog = Dialog(this)
        var dialogBinding = DialogAddNewItemBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialogBinding.txtDialogTitle.text = "Add Medicine"

        dialogBinding.imgClose.setOnClickListener {
            dialog.dismiss()
            dialogBinding.edtName.setText("")
        }

        dialogBinding.btnSubmit.text = "Submit"
        dialogBinding.cdDelete.visibility = View.GONE
        dialog.show()
        dialogBinding.btnSubmit.setOnClickListener {
            var name = dialogBinding.edtName.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please Enter Medicine Name", Toast.LENGTH_SHORT).show()
            } else {
                var uid = UUID.randomUUID().toString()
                progressBarDialog.show()
                mDbRef.child("MedicineList").child(uid)
                    .setValue(ModelClass(name, uid))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Record Save Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            addPatientMedicineFun(ModelClass(name, uid))
                            dialogBinding.edtName.setText("")
                            progressBarDialog.dismiss()
                            dialog.dismiss()
                            diseaseDialog.dismiss()

                        }
                    }.addOnFailureListener {
                        progressBarDialog.dismiss()
                        dialog.dismiss()

                    }

            }
        }
    }

    private fun addPatientMedicineFun(model: ModelClass) {
        var dialog = Dialog(this)
        var dialogBinding = DialogAddMedicineBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialogBinding.txtMedicineName.text = model.name

        dialogBinding.imgClose.setOnClickListener {
            dialog.dismiss()
            dialogBinding.edtMedicineTime.setText("")
            dialogBinding.edtMedicineQty.setText("")
        }

        dialogBinding.cdDelete.visibility = View.GONE
        dialog.show()
        dialogBinding.btnSubmit.setOnClickListener {
            var name = dialogBinding.txtMedicineName.text.toString()
            var qty = dialogBinding.edtMedicineQty.text.toString()
            var medicineTime = dialogBinding.edtMedicineTime.text.toString()


            progressBarDialog.show()
            mDbRef.child("PatientList").child(patientUid!!)
                .child("PatientCheckUpDetails").child(currentDateToday!!)
                .child("PatientMedicine").child(model.uid!!)
                .setValue(PatientMedicineModel(name, qty, medicineTime, model.uid!!))
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Record Save Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        dialogBinding.edtMedicineTime.setText("")
                        dialogBinding.edtMedicineQty.setText("")
                        progressBarDialog.dismiss()
                        dialog.dismiss()


                    }
                }.addOnFailureListener {
                    progressBarDialog.dismiss()
                    dialog.dismiss()

                }

        }
    }


    private fun searchMedicineFun() {
        diseaseDialogBinding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchMedicineItems(it) }
                return true
            }
        })
    }

    private fun searchMedicineItems(query: String) {
        mDbRef.child("MedicineList").orderByChild("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<ModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ModelClass::class.java)
                        item?.let {

                            searchItems.add(it)


                        }
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
                }
            })
    }

    private fun patientCheckUpMedicineEditFun(model: PatientMedicineModel) {
        var dialog = Dialog(this)
        var dialogBinding = DialogAddMedicineBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialogBinding.txtMedicineName.text = model.name
        dialogBinding.edtMedicineQty.setText(model.qty)
        dialogBinding.edtMedicineTime.setText(model.time)

        dialogBinding.imgClose.setOnClickListener {
            dialog.dismiss()
            dialogBinding.edtMedicineTime.setText("")
            dialogBinding.edtMedicineQty.setText("")
        }

        dialogBinding.cdDelete.visibility = View.VISIBLE
        dialogBinding.cdDelete.setOnClickListener {
            patientCheckUpMedicineDeleteFun(model.uid!!)
            dialog.dismiss()
        }
        dialog.show()
        dialogBinding.btnSubmit.setOnClickListener {
            var name = dialogBinding.txtMedicineName.text.toString()
            var qty = dialogBinding.edtMedicineQty.text.toString()
            var medicineTime = dialogBinding.edtMedicineTime.text.toString()


            progressBarDialog.show()
            mDbRef.child("PatientList").child(patientUid!!)
                .child("PatientCheckUpDetails").child(currentDateToday!!)
                .child("PatientMedicine").child(model.uid!!)
                .setValue(PatientMedicineModel(name, qty, medicineTime, model.uid!!))
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Record Save Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        dialogBinding.edtMedicineTime.setText("")
                        dialogBinding.edtMedicineQty.setText("")
                        progressBarDialog.dismiss()
                        dialog.dismiss()


                    }
                }.addOnFailureListener {
                    progressBarDialog.dismiss()
                    dialog.dismiss()

                }

        }
    }

    private fun patientCheckUpMedicineDeleteFun(medicineUid: String) {
        var deleteDialog = Dialog(this)

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("PatientList").child(patientUid!!)
                .child("PatientCheckUpDetails").child(currentDateToday!!).child("PatientMedicine")
                .child(medicineUid)
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

    private fun reportImageListFun() {


        binding.cdAddReportImage.setOnClickListener {

            dialogReportImageFun()

        }

        val reportList = ArrayList<ReportModelClass>()

        mDbRef.child("PatientList").child(patientUid!!)
            .child("PatientCheckUpDetails").child(currentDateToday!!).child("PatientReportImage")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reportList.clear()
                    for (i in snapshot.children) {
                        val data = i.getValue(ReportModelClass::class.java)
                        data?.let { reportList.add(it) }
                    }
                    val reportAdapter = ReportAdapter(this@PatientCheckUpActivity) {
                        var i = Intent(this@PatientCheckUpActivity, ReportViewActivity::class.java)
                        i.putExtra("patientUid", patientUid)
                        i.putExtra("reportUid", it.reportUid)
                        i.putExtra("currentDateToday", currentDateToday)
                        startActivity(i)
                    }
                    binding.rcvReportImageList.layoutManager =
                        LinearLayoutManager(
                            this@PatientCheckUpActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    binding.rcvReportImageList.adapter = reportAdapter
                    if (reportList.isEmpty()) {
                        binding.linReportNoDataFound.visibility = View.VISIBLE
                    } else {
                        binding.linReportNoDataFound.visibility = View.GONE
                    }
                    reportAdapter.updateList(reportList)

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

    private fun dialogReportImageFun() {
        reportImage = Drawable.createFromPath(R.drawable.ic_image.toString()).toString()

        reportImageDialog = Dialog(this)
        reportImageDialogBinding = DialogAddPatientReportImageBinding.inflate(layoutInflater)
        reportImageDialog.setContentView(reportImageDialogBinding.root)
        reportImageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        reportImageDialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        reportImageDialogBinding.imgClose.setOnClickListener {
            if (imageUploadCompleted == 0) {
                reportImageDialog.dismiss()
            } else {
                if (imageAndNameSaveCompleted == 1) {
                    reportImageDialog.dismiss()
                } else {
                    Toast.makeText(this, "First Image Save", Toast.LENGTH_SHORT).show()
                }
            }

        }
        reportImageDialogBinding.cdAddImage.setOnClickListener {
            selectedImageDialog()
        }

        reportImageDialogBinding.cdSave.setOnClickListener {

            var reportImage = reportImage
            var reportName = reportImageDialogBinding.edtReportName.text.toString()

            progressBarDialog.show()
            var reportUid = UUID.randomUUID().toString()
            progressBarDialog.show()
            mDbRef.child("PatientList").child(patientUid!!)
                .child("PatientCheckUpDetails").child(currentDateToday!!)
                .child("PatientReportImage").child(reportUid).setValue(
                    ReportModelClass(
                        reportImage!!,
                        reportName,
                        reportUid
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Record Save Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        imageAndNameSaveCompleted = 1
                        progressBarDialog.dismiss()
                        reportImageDialog.dismiss()


                    }
                }.addOnFailureListener {

                    progressBarDialog.dismiss()
                    reportImageDialog.dismiss()

                }

        }


        reportImageDialogBinding.cdUploadImage.setOnClickListener {
            if (reportImagePath == null) {

                Toast.makeText(this, "Please Select Report Image", Toast.LENGTH_SHORT).show()

            } else {
                imageUpload()
            }
        }
        reportImageDialog.setCancelable(false)
        reportImageDialog.show()
    }

    private fun selectedImageDialog() {
        val dialog = Dialog(this)
        val custtomeDialogBinding: ImageSelctedDialogBinding =
            ImageSelctedDialogBinding.inflate(
                layoutInflater
            )
        dialog.setContentView(custtomeDialogBinding.getRoot())


        //camera
        custtomeDialogBinding.layCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            camera_Launcher.launch(intent)
            dialog.dismiss()
        }

        //gallery
        custtomeDialogBinding.layGallery.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            gallery_Launcher.launch(intent)
            dialog.dismiss()
        }

        //cancel
        custtomeDialogBinding.cdCancel.setOnClickListener { dialog.dismiss() }

        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }

    // Camera launcher
    var camera_Launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent = result.data!!
                val imageBitmap = data.extras?.getParcelable("data") as Bitmap?

                reportImagePath = getImageUri(applicationContext, imageBitmap!!)


                reportImageDialogBinding.imgReportImage.setImageBitmap(imageBitmap)

            }
        }

    // Function to convert Bitmap to Uri
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    //gallery
    var gallery_Launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent = result.data!!


                reportImagePath = data.data!!
                reportImageDialogBinding.imgReportImage.setImageURI(reportImagePath)

            }
        })

    private fun imageUpload() {
        if (reportImagePath != null) {


            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            progressDialog.setCancelable(false)

            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "patientImage/reports/"
                            + UUID.randomUUID().toString()
                )


            // adding listeners on upload
            // or failure of image
            ref.putFile(reportImagePath!!).addOnCompleteListener {

//                it.result.uploadSessionUri

                ref.downloadUrl.addOnSuccessListener {

                    reportImage = it.toString()
                }
            }
                .addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                    imageUploadCompleted = 1
                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded " + progress.toInt() + "%"
                    )
                }
        }
    }

    private fun saveDataAndAppointmentsCompleted() {
        var dialog = Dialog(this)
        var dialogBinding = DialogAppointmentsSaveBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialogBinding.btnCansel.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnYes.setOnClickListener {
            progressBarDialog.show()
            patientCheckUpCompleteFun()
            removePatientAppointment()
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun patientCheckUpCompleteFun() {
        var patientImage: String
        var patientName: String
        var patientAge: String
        var patientWeight: String
        var patientMobileNo: String
        var patientVillage: String
        var patientGender: String
        var timestamp: String
        var appointmentsNumber: Int

        mDbRef.child("AppointmentList").child(patientUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val patientItem = snapshot.getValue(PatientModelClass::class.java)
                        if (patientItem != null) {
                            // User data retrieved successfully
                            val patientUid = patientItem.patientUid
                            patientImage = patientItem.patientImage!!
                            patientName = patientItem.patientName!!
                            patientAge = patientItem.patientAge!!
                            patientWeight = patientItem.patientWeight!!
                            patientMobileNo = patientItem.patientMobileNo!!
                            patientVillage = patientItem.patientVillage!!
                            patientGender = patientItem.patientGender!!
                            timestamp = patientItem.timestamp!!
                            appointmentsNumber = patientItem.appointmentsNumber


                            mDbRef.child("AppointmentCompletedList").child(patientUid!!).setValue(
                                PatientModelClass(
                                    patientImage,
                                    patientName,
                                    patientAge,
                                    patientWeight,
                                    patientMobileNo,
                                    patientVillage,
                                    patientGender,
                                    patientUid,
                                    timestamp,
                                    appointmentsNumber
                                )
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this@PatientCheckUpActivity,
                                        "Record Save Successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    progressBarDialog.dismiss()
                                    finish()

                                }
                            }.addOnFailureListener {

                                progressBarDialog.dismiss()

                            }

                            mDbRef.child("PatientHistoryData").child(currentDateToday!!)
                                .child(patientUid).setValue(
                                    PatientModelClass(
                                        patientImage,
                                        patientName,
                                        patientAge,
                                        patientWeight,
                                        patientMobileNo,
                                        patientVillage,
                                        patientGender,
                                        patientUid,
                                        timestamp,
                                        appointmentsNumber
                                    )
                                ).addOnCompleteListener {
                                    if (it.isSuccessful) {

                                        progressBarDialog.dismiss()
                                        finish()

                                    }
                                }.addOnFailureListener {

                                    progressBarDialog.dismiss()

                                }
                        }
                    } else {
                        // User data does not exist
                        Toast.makeText(
                            this@PatientCheckUpActivity,
                            "No Patient",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    private fun removePatientAppointment() {
        mDbRef.child("AppointmentList").child(patientUid!!).removeValue()
    }
}
