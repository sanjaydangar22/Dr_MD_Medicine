package com.sdsoft.drmdmedicine.Admin_panel.report

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sdsoft.drmdmedicine.Admin_panel.activity.AdminHomeActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.databinding.ActivityAddReportBinding
import com.sdsoft.drmdmedicine.databinding.ImageSelctedDialogBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddReportActivity : AppCompatActivity() {


    lateinit var addReportBinding: ActivityAddReportBinding
    lateinit var progressBarDialog: ProgressBarDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    var reportImagePath: Uri? = null

    var imageUploadCompleted = 0
    var selectedImage = 0


    var patientUid: String? = null
    var reportImage: String? = null


    var flag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addReportBinding = ActivityAddReportBinding.inflate(layoutInflater)
        setContentView(addReportBinding.root)

        progressBarDialog = ProgressBarDialog(this)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {
        addReportBinding.imgBack.setOnClickListener {
            onBackPressed()
        }

        addReportBinding.cdAddImage.setOnClickListener {

            selectedImage = 1
            selectedImageDialog()
        }

        addReportBinding.cdUploadImage.setOnClickListener {

            if (reportImagePath == null) {

                Toast.makeText(this, "Please Select Report Image", Toast.LENGTH_SHORT).show()

            } else {
                imageUpload()
            }
        }



        addReportBinding.cdSave.setOnClickListener {

            if (imageUploadCompleted == 1) {

                var reportImage = reportImage
                var reportName = addReportBinding.edtReportName.text.toString()



                if (reportName.isEmpty()) {
                    Toast.makeText(this, "Report Name is empty", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    var reportUid = UUID.randomUUID().toString()
                    progressBarDialog.show()
                    mDbRef.child("PatientList").child(patientUid!!).child("Reports").child(reportUid).setValue(
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

                            var i = Intent(this, AdminHomeActivity::class.java)
                            startActivity(i)
                            progressBarDialog.dismiss()
                        }
                    }.addOnFailureListener {
                        Log.e("TAG", "fail: " + it.message)
                        progressBarDialog.dismiss()

                    }

                }


            } else {
                Toast.makeText(this, "First Image Upload", Toast.LENGTH_SHORT).show()
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
                reportImagePath = getImageUri(applicationContext, imageBitmap!!)
                Log.e("TAG", "reportImagePath:  $reportImagePath")

                addReportBinding.imgReportImage.setImageBitmap(imageBitmap)

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
                addReportBinding.imgReportImage.setImageURI(reportImagePath)

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
                    Log.e("TAG", "uploadImage: " + reportImage)
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