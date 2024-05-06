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
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityAddPatientBinding
import com.sdsoft.drmdmedicine.databinding.ImageSelctedDialogBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddPatientActivity : BaseActivity(R.layout.activity_add_patient) {

    lateinit var addPatientBinding: ActivityAddPatientBinding

    lateinit var storageReference: StorageReference

    var patientImagePath: Uri? = null


    var patientUid: String? = null
    var patientImage: String? = null
    var patientGender: String? = null
    var timestamp: String = ""
    var appointmentsNumber: Int = 0


    var flag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPatientBinding = ActivityAddPatientBinding.inflate(layoutInflater)
        setContentView(addPatientBinding.root)

        progressBarDialog = ProgressBarDialog(this)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {
        addPatientBinding.imgBack.setOnClickListener {
            onBackPressed()
        }

        patientImage = Drawable.createFromPath(R.drawable.user_icon.toString()).toString()

        if (intent != null && intent.hasExtra("itemUpdate")) {  // data update key access this class

            flag = 1


            patientUid = intent.getStringExtra("patientUid")   // key set  variable

            mDbRef.child("PatientList").child(patientUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val patientItem = snapshot.getValue(PatientModelClass::class.java)
                            if (patientItem != null) {
                                // User data retrieved successfully
                                val patientUid = patientItem.patientUid
                                patientImage = patientItem.patientImage
                                val patientName = patientItem.patientName
                                val patientAge = patientItem.patientAge
                                val patientWeight = patientItem.patientWeight
                                val patientMobileNo = patientItem.patientMobileNo
                                val patientVillage = patientItem.patientVillage
                                patientGender = patientItem.patientGender

                                Glide.with(this@AddPatientActivity).load(patientImage)
                                    .placeholder(R.drawable.ic_image)
                                    .into(addPatientBinding.imgPatientImage)

                                addPatientBinding.edtPatientName.setText(patientName.toString())
                                addPatientBinding.edtPatientAge.setText(patientAge.toString())
                                addPatientBinding.edtPatientWeight.setText(patientWeight.toString())
                                addPatientBinding.edtPatientMobileNo.setText(patientMobileNo.toString())
                                addPatientBinding.edtPatientVillage.setText(patientVillage.toString())

                                if (patientGender == "Male") {
                                    "Golden"
                                    addPatientBinding.rbMale.isChecked = true
                                } else if (patientGender == "Female") {
                                    "Female"
                                    addPatientBinding.rbFemale.isChecked = true
                                } else {
                                    "Other"
                                    addPatientBinding.rbOther.isChecked = true
                                }

                                //change page title
                                addPatientBinding.txtTitle.text = "Patient Edit"
                                //change button name
                                addPatientBinding.txtSave.text = "Update"

                                patientImagePath = Uri.parse(patientImage)

                            }
                        } else {
                            // User data does not exist
                            Toast.makeText(
                                this@AddPatientActivity,
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

        if (intent != null && intent.hasExtra("addNewAppointment")) {  // data update key access this class

            flag = 2


            patientUid = intent.getStringExtra("patientUid")   // key set  variable
            timestamp = intent.getStringExtra("timestamp").toString()   // key set  variable
            appointmentsNumber = intent.getIntExtra("appointmentsNumber", 0)   // key set  variable


            mDbRef.child("PatientList").child(patientUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val patientItem = snapshot.getValue(PatientModelClass::class.java)
                            if (patientItem != null) {
                                // User data retrieved successfully
                                val patientUid = patientItem.patientUid
                                patientImage = patientItem.patientImage
                                val patientName = patientItem.patientName
                                val patientAge = patientItem.patientAge
                                val patientWeight = patientItem.patientWeight
                                val patientMobileNo = patientItem.patientMobileNo
                                val patientVillage = patientItem.patientVillage
                                patientGender = patientItem.patientGender


                                if (!isDestroyed) {
                                    Glide.with(this@AddPatientActivity)
                                        .load(patientImage)
                                        .placeholder(R.drawable.ic_image)
                                        .into(addPatientBinding.imgPatientImage)
                                }

                                addPatientBinding.edtPatientName.setText(patientName.toString())
                                addPatientBinding.edtPatientAge.setText(patientAge.toString())
                                addPatientBinding.edtPatientWeight.setText(patientWeight.toString())
                                addPatientBinding.edtPatientMobileNo.setText(patientMobileNo.toString())
                                addPatientBinding.edtPatientVillage.setText(patientVillage.toString())

                                if (patientGender == "Male") {
                                    "Golden"
                                    addPatientBinding.rbMale.isChecked = true
                                } else if (patientGender == "Female") {
                                    "Female"
                                    addPatientBinding.rbFemale.isChecked = true
                                } else {
                                    "Other"
                                    addPatientBinding.rbOther.isChecked = true
                                }

                                //change page title
                                addPatientBinding.txtTitle.text = "Add Appointments"
                                //change button name
                                addPatientBinding.txtSave.text = "Save"

                                patientImagePath = Uri.parse(patientImage)

                            }
                        } else {
                            // User data does not exist
                            Toast.makeText(
                                this@AddPatientActivity,
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

        if (intent != null && intent.hasExtra("addNewAppointmentWithPatient")) {  // data update key access this class

            flag = 3

            timestamp = intent.getStringExtra("timestamp").toString()   // key set  variable
            appointmentsNumber = intent.getIntExtra("appointmentsNumber", 0)   // key set  variable

        }

        if (intent != null && intent.hasExtra("itemUpdatePatientCheckUp")) {  // data update key access this class

            flag = 4


            patientUid = intent.getStringExtra("patientUid")   // key set  variable

            mDbRef.child("PatientList").child(patientUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val patientItem = snapshot.getValue(PatientModelClass::class.java)
                            if (patientItem != null) {
                                // User data retrieved successfully
                                val patientUid = patientItem.patientUid
                                patientImage = patientItem.patientImage
                                val patientName = patientItem.patientName
                                val patientAge = patientItem.patientAge
                                val patientWeight = patientItem.patientWeight
                                val patientMobileNo = patientItem.patientMobileNo
                                val patientVillage = patientItem.patientVillage
                                patientGender = patientItem.patientGender

                                Glide.with(this@AddPatientActivity).load(patientImage)
                                    .placeholder(R.drawable.ic_image)
                                    .into(addPatientBinding.imgPatientImage)

                                addPatientBinding.edtPatientName.setText(patientName.toString())
                                addPatientBinding.edtPatientAge.setText(patientAge.toString())
                                addPatientBinding.edtPatientWeight.setText(patientWeight.toString())
                                addPatientBinding.edtPatientMobileNo.setText(patientMobileNo.toString())
                                addPatientBinding.edtPatientVillage.setText(patientVillage.toString())

                                if (patientGender == "Male") {
                                    "Golden"
                                    addPatientBinding.rbMale.isChecked = true
                                } else if (patientGender == "Female") {
                                    "Female"
                                    addPatientBinding.rbFemale.isChecked = true
                                } else {
                                    "Other"
                                    addPatientBinding.rbOther.isChecked = true
                                }

                                //change page title
                                addPatientBinding.txtTitle.text = "Patient Edit"
                                //change button name
                                addPatientBinding.txtSave.text = "Update"

                                patientImagePath = Uri.parse(patientImage)

                            }
                        } else {
                            // User data does not exist
                            Toast.makeText(
                                this@AddPatientActivity,
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
        addPatientBinding.imgAddPatientImage.setOnClickListener {


            selectedImageDialog()
        }

        addPatientBinding.cdUploadImage.setOnClickListener {

            if (patientImagePath == null) {

                Toast.makeText(this, "Please Select Patient Image", Toast.LENGTH_SHORT).show()

            } else {
                imageUpload()
            }
        }

        //        patientGender = "Male"
        if (patientGender == null) {  // variable set in if statement
            addPatientBinding.rbMale.isChecked = true// set radio button Male true
        }

        addPatientBinding.cdSave.setOnClickListener {


            var patientImage = patientImage
            var patientName = addPatientBinding.edtPatientName.text.toString()
            var patientAge = addPatientBinding.edtPatientAge.text.toString()
            var patientWeight = addPatientBinding.edtPatientWeight.text.toString()
            var patientMobileNo = addPatientBinding.edtPatientMobileNo.text.toString()
            var patientVillage = addPatientBinding.edtPatientVillage.text.toString()

            if (addPatientBinding.rgGender.checkedRadioButtonId == -1) {

            } else {
                val selectId: Int = addPatientBinding.rgGender.checkedRadioButtonId
                var selectedRadioButton: RadioButton = findViewById(selectId)
                var text = selectedRadioButton.text.toString()

                patientGender = if (text == "Male") {
                    "Male"
                } else if (text == "Female") {
                    "Female"
                } else {
                    "Other"
                }

            }

            if (flag == 1) {
                timestamp = ""
                progressBarDialog.show()
                mDbRef.child("PatientList").child(patientUid!!).setValue(
                    PatientModelClass(
                        patientImage!!,
                        patientName,
                        patientAge,
                        patientWeight,
                        patientMobileNo,
                        patientVillage,
                        patientGender!!,
                        patientUid!!,
                        timestamp,
                        appointmentsNumber
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Record Save Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        var i = Intent(this, AdminHomeActivity::class.java)
                        startActivity(i)
                        progressBarDialog.dismiss()
                    }
                }.addOnFailureListener {
                    progressBarDialog.dismiss()

                }
            } else if (flag == 2) {
                progressBarDialog.show()
                mDbRef.child("AppointmentList").child(patientUid!!).setValue(
                    PatientModelClass(
                        patientImage!!,
                        patientName,
                        patientAge,
                        patientWeight,
                        patientMobileNo,
                        patientVillage,
                        patientGender!!,
                        patientUid!!,
                        timestamp!!,
                        appointmentsNumber
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Record Save Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        mDbRef.child("AppointmentNumber").setValue(appointmentsNumber)
                        progressBarDialog.dismiss()
                        var i = Intent(this, AppointmentsActivity::class.java)
                        startActivity(i)
                        finish()

                    }
                }.addOnFailureListener {
                    progressBarDialog.dismiss()

                }
            } else if (flag == 3) {

                var patientUid = UUID.randomUUID().toString()
                progressBarDialog.show()
                mDbRef.child("PatientList").child(patientUid).setValue(
                    PatientModelClass(
                        patientImage!!,
                        patientName,
                        patientAge,
                        patientWeight,
                        patientMobileNo,
                        patientVillage,
                        patientGender!!,
                        patientUid,
                        timestamp!!,
                        0
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        mDbRef.child("AppointmentList").child(patientUid).setValue(
                            PatientModelClass(
                                patientImage!!,
                                patientName,
                                patientAge,
                                patientWeight,
                                patientMobileNo,
                                patientVillage,
                                patientGender!!,
                                patientUid,
                                timestamp!!,
                                appointmentsNumber
                            )
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Record Save Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                progressBarDialog.dismiss()
                                mDbRef.child("AppointmentNumber").setValue(appointmentsNumber)
                                var i = Intent(this, AppointmentsActivity::class.java)
                                startActivity(i)
                                finish()

                            }
                        }.addOnFailureListener {
                            progressBarDialog.dismiss()

                        }

                    }
                }.addOnFailureListener {
                    progressBarDialog.dismiss()

                }


            } else if (flag == 4) {
                timestamp = ""
                progressBarDialog.show()
                mDbRef.child("PatientList").child(patientUid!!).setValue(
                    PatientModelClass(
                        patientImage!!,
                        patientName,
                        patientAge,
                        patientWeight,
                        patientMobileNo,
                        patientVillage,
                        patientGender!!,
                        patientUid!!,
                        timestamp!!,
                        appointmentsNumber
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Record Save Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        var i = Intent(this, PatientCheckUpActivity::class.java)
                        i.putExtra("patientUid", patientUid)
                        startActivity(i)
                        progressBarDialog.dismiss()
                    }
                }.addOnFailureListener {
                    progressBarDialog.dismiss()

                }
            } else {

                var patientUid = UUID.randomUUID().toString()
                progressBarDialog.show()
                mDbRef.child("PatientList").child(patientUid).setValue(
                    PatientModelClass(
                        patientImage!!,
                        patientName,
                        patientAge,
                        patientWeight,
                        patientMobileNo,
                        patientVillage,
                        patientGender!!,
                        patientUid,
                        timestamp,
                        appointmentsNumber
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Record Save Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        var i = Intent(this, AdminHomeActivity::class.java)
                        startActivity(i)
                        progressBarDialog.dismiss()
                    }
                }.addOnFailureListener {
                    progressBarDialog.dismiss()

                }
            }
        }


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


//
                patientImagePath = getImageUri(applicationContext, imageBitmap!!)

                addPatientBinding.imgPatientImage.setImageBitmap(imageBitmap)

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

                patientImagePath = data.data!!
                addPatientBinding.imgPatientImage.setImageURI(patientImagePath)

            }
        })


    private fun imageUpload() {
        if (patientImagePath != null) {


            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            progressDialog.setCancelable(false)

            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "patientImage/"
                            + UUID.randomUUID().toString()
                )


            // adding listeners on upload
            // or failure of image
            ref.putFile(patientImagePath!!).addOnCompleteListener {

//                it.result.uploadSessionUri

                ref.downloadUrl.addOnSuccessListener {

                    patientImage = it.toString()
                }
            }
                .addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show()

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