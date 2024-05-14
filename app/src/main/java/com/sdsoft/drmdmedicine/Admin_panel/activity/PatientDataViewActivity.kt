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
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
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
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.DiseaseListAdapter
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
import com.sdsoft.drmdmedicine.databinding.DialogAddNewItemBinding
import com.sdsoft.drmdmedicine.databinding.DialogAddPatientReportImageBinding
import com.sdsoft.drmdmedicine.databinding.DialogShowListAndAddNewItemBinding
import com.sdsoft.drmdmedicine.databinding.ImageSelctedDialogBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class PatientDataViewActivity : BaseActivity(R.layout.activity_patient_data_view) {

    lateinit var patientDataViewBinding: ActivityPatientDataViewBinding
    lateinit var storageReference: StorageReference
    lateinit var patientUid: String
    private lateinit var diseaseDialog: Dialog
    private lateinit var diseaseDialogBinding: DialogShowListAndAddNewItemBinding
    private lateinit var adapter: DiseaseListAdapter

    var imageUploadCompleted = 0
    var imageAndNameSaveCompleted = 0
    var reportImagePath: Uri? = null
    lateinit var reportImageDialog: Dialog
    lateinit var reportImageDialogBinding: DialogAddPatientReportImageBinding

    var reportImage: String? = null
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


        dialogFun()
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

    private fun patientCheckUpDetailsShowFun(patientCheckUpDetails: PatientCheckUpDetails) {

        patientDataViewBinding.linNotSelectAnyDate.visibility = View.GONE
        patientDataViewBinding.scrollView.visibility = View.VISIBLE

        patientDataViewBinding.cdAddDisease.setOnClickListener {
            addDiseaseDialog(patientCheckUpDetails.date!!)
        }
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
                        patientCheckUpDiseaseDeleteFun(it, patientCheckUpDetails.date!!)
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


        patientDataViewBinding.cdAddMedicine.setOnClickListener {
            addMedicineDialog(patientCheckUpDetails.date!!)
        }
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
                        patientCheckUpMedicineDeleteFun(it, patientCheckUpDetails.date!!)
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



        patientDataViewBinding.cdAddReportImage.setOnClickListener {

            dialogReportImageFun(patientCheckUpDetails.date!!)

        }
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


    private fun addDiseaseDialog(date: String) {
        diseaseDialogBinding.txtDialogTitle.text = "Add New Disease"
        searchDiseaseFun()
        diseaseListShowFun(date)
        diseaseDialogBinding.imgAddNewItem.setOnClickListener {
            addNewDiseaseFun(date)
        }

        diseaseDialog.setCancelable(false)
        diseaseDialog.show()
    }

    private fun diseaseListShowFun(date: String) {
        adapter = DiseaseListAdapter(this) {
            addPatientDiseaseFun(it, date)
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
                        this@PatientDataViewActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun addNewDiseaseFun(date: String) {
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
                            addPatientDiseaseFun(ModelClass(name, uid), date)
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

    private fun addPatientDiseaseFun(model: ModelClass, date: String) {
        mDbRef.child("PatientList").child(patientUid)
            .child("PatientCheckUpDetails").child(date)
            .child("PatientDisease").child(model.uid!!).setValue(model)
    }

    private fun searchDiseaseItems(query: String) {
        mDbRef.child("DiseaseList").orderByChild("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<ModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ModelClass::class.java)
                        if (item!!.name!!.lowercase().contains(query.lowercase())) {
                            searchItems.add(item)
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


    private fun addMedicineDialog(date: String) {
        diseaseDialogBinding.txtDialogTitle.text = "Add New Medicine"
        searchMedicineFun()
        medicineListShowFun(date)
        diseaseDialogBinding.imgAddNewItem.setOnClickListener {
            addNewMedicineFun(date)
        }

        diseaseDialog.setCancelable(false)
        diseaseDialog.show()
    }

    private fun medicineListShowFun(date: String) {
        adapter = DiseaseListAdapter(this) {
            addPatientMedicineFun(it, date)
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
                        this@PatientDataViewActivity,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun addNewMedicineFun(date: String) {
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
                            addPatientMedicineFun(ModelClass(name, uid), date)
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

    private fun addPatientMedicineFun(model: ModelClass, date: String) {
        mDbRef.child("PatientList").child(patientUid)
            .child("PatientCheckUpDetails").child(date)
            .child("PatientMedicine").child(model.uid!!).setValue(model)
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
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<ModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ModelClass::class.java)
                        if (item!!.name!!.lowercase().contains(query.lowercase())) {
                            searchItems.add(item)
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


    private fun dialogReportImageFun(date: String) {

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
            mDbRef.child("PatientList").child(patientUid)
                .child("PatientCheckUpDetails").child(date)
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
                    Log.e("TAG", "fail: " + it.message)
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

//                var selectedImageUri = getImagePathFromURI(uri!!)

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


}