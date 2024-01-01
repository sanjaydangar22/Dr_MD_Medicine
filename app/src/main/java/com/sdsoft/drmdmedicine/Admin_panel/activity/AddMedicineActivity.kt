package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.storage.UploadTask
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityAddMedicineBinding
import com.sdsoft.drmdmedicine.databinding.ImageSelctedDialogBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddMedicineActivity : AppCompatActivity() {
    lateinit var addMedicineBinding: ActivityAddMedicineBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    lateinit var progressBarDialog: ProgressBarDialog

    var frontImagePath: Uri? = null
    var backImagePath: Uri? = null

    var imageUploadCompleted = 0
    var selectedImage = 0


    var medicineUid: String? = null
    var frontImage: String? = null
    var backImage: String? = null

    var flag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addMedicineBinding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(addMedicineBinding.root)

        progressBarDialog = ProgressBarDialog(this)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {
        addMedicineBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
        if (intent != null && intent.hasExtra("itemUpdate")) {  // data update key access this class

            flag = 1
            imageUploadCompleted = 1

            medicineUid = intent.getStringExtra("medicineUid")   // key set  variable

            Log.e("TAG", "medicineUid: " + medicineUid)


            mDbRef.child("MedicineList").child(medicineUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val medicineItem = snapshot.getValue(MedicineModelClass::class.java)
                            if (medicineItem != null) {
                                // User data retrieved successfully
                                val medicineUid = medicineItem.medicineUid
                                frontImage = medicineItem.frontImage
                                backImage = medicineItem.backImage
                                val medicineCompanyName = medicineItem.medicineCompanyName
                                val medicineName = medicineItem.medicineName
                                var medicineUse = medicineItem.medicineUse




                                Log.e("TAG", "frontImage:  $frontImage ")
                                Log.e("TAG", "medicineName:  $medicineName ")




                                Glide.with(this@AddMedicineActivity).load(frontImage)
                                    .placeholder(R.drawable.ic_image)
                                    .into(addMedicineBinding.imgFrontImage)
                                Glide.with(this@AddMedicineActivity).load(backImage)
                                    .placeholder(R.drawable.ic_image)
                                    .into(addMedicineBinding.imgBackImage)

                                addMedicineBinding.edtMedicineCompanyName.setText(
                                    medicineCompanyName.toString()
                                )
                                addMedicineBinding.edtMedicineName.setText(medicineName.toString())
                                addMedicineBinding.edtMedicineUse.setText(medicineUse.toString())

                                //change page title
                                addMedicineBinding.txtTitle.text = "Medicine Edit"
                                //change button name
                                addMedicineBinding.txtSave.text = "Update"

                                frontImagePath = Uri.parse(frontImage)
                                backImagePath = Uri.parse(backImage)
                            }
                        } else {
                            // User data does not exist
                            Toast.makeText(
                                this@AddMedicineActivity,
                                "No Medicine",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
        addMedicineBinding.imgAddFrontImage.setOnClickListener {

            selectedImage = 1
            selectedImageDialog()
        }
        addMedicineBinding.imgAddBackImage.setOnClickListener {

            selectedImage = 2
            selectedImageDialog()
        }

        addMedicineBinding.cdUploadImage.setOnClickListener {

            if (frontImagePath == null) {

                Toast.makeText(this, "Please Select Front Image", Toast.LENGTH_SHORT).show()

            } else if (backImagePath == null) {

                Toast.makeText(this, "Please Select Back Image", Toast.LENGTH_SHORT).show()

            } else {
                imageUpload()
            }
        }

        addMedicineBinding.cdSave.setOnClickListener {

            if (imageUploadCompleted == 1) {

                var frontImage = frontImage
                var backImage = backImage
                var medicineCompanyName = addMedicineBinding.edtMedicineCompanyName.text.toString()
                var medicineName = addMedicineBinding.edtMedicineName.text.toString()
                var medicineUse = addMedicineBinding.edtMedicineUse.text.toString()


                if (medicineCompanyName.isEmpty()) {
                    Toast.makeText(this, "Medicine Company name is empty", Toast.LENGTH_SHORT)
                        .show()
                } else if (medicineName.isEmpty()) {
                    Toast.makeText(this, "Medicine name is empty", Toast.LENGTH_SHORT).show()
                } else if (medicineUse.isEmpty()) {
                    Toast.makeText(this, "Medicine use is empty", Toast.LENGTH_SHORT).show()
                } else {
                    if (flag == 1) {
                        progressBarDialog.show()
                        mDbRef.child("MedicineList").child(medicineUid!!).setValue(
                            MedicineModelClass(
                                frontImage!!,
                                backImage!!,
                                medicineCompanyName,
                                medicineName,
                                medicineUse,
                                medicineUid!!
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
                    } else {

                        var medicineUid = UUID.randomUUID().toString()
                        progressBarDialog.show()
                        mDbRef.child("MedicineList").child(medicineUid).setValue(
                            MedicineModelClass(
                                frontImage!!,
                                backImage!!,
                                medicineCompanyName,
                                medicineName,
                                medicineUse,
                                medicineUid
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

                if (selectedImage == 1) {
                    // Check if data.data is not null, otherwise use data.extras
//
                    frontImagePath = getImageUri(applicationContext, imageBitmap!!)
                    Log.e("TAG", "frontImagePath:  $frontImagePath")

                    addMedicineBinding.imgFrontImage.setImageBitmap(imageBitmap)
                } else if (selectedImage == 2) {
                    // Check if data.data is not null, otherwise use data.extras
                    backImagePath = getImageUri(applicationContext, imageBitmap!!)
                    Log.e("TAG", "backImagePath:  $backImagePath")
                    addMedicineBinding.imgBackImage.setImageBitmap(imageBitmap)
                }
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
                if (selectedImage == 1) {
                    frontImagePath = data.data!!
                    addMedicineBinding.imgFrontImage.setImageURI(frontImagePath)
                } else if (selectedImage == 2) {
                    backImagePath = data.data!!
                    addMedicineBinding.imgBackImage.setImageURI(backImagePath)
                }
            }
        })


    private fun imageUpload() {
        if (frontImagePath != null && backImagePath != null) {


            val imagesBitmap = listOf(frontImagePath!!, backImagePath!!)
            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            progressDialog.setCancelable(false)

            // Defining the child of storageReference
            val ref = storageReference.child("medicineImage/" + UUID.randomUUID().toString())

            val uploadTasks = mutableListOf<UploadTask>()
            val downloadUrls = mutableListOf<String>()
            // Upload each image in the list
            imagesBitmap.forEachIndexed { index, imageUri ->
                // Adding listeners on upload or failure of the image
                val uploadTask = ref.child("image${index + 1}").putFile(imageUri!!)

                uploadTask.addOnSuccessListener {
                    // Image uploaded successfully
                    // You can get the download URL or perform other operations here
                    it.storage.downloadUrl.addOnSuccessListener { uri ->

                        val imageUrl = uri.toString()
                        downloadUrls.add(imageUrl)

                        if (downloadUrls.size == imagesBitmap.size) {
                            // Both images uploaded, you can use downloadUrls[0] and downloadUrls[1]
                            progressDialog.dismiss()
                            Toast.makeText(this, "Images Uploaded!!", Toast.LENGTH_SHORT).show()

                            // Handle the download URLs as needed (e.g., save to database)
                            val frontImageUrl = downloadUrls[0]
                            val backImageUrl = downloadUrls[1]

                            Log.e("TAG", "frontImageUrl:  $frontImageUrl ")
                            Log.e("TAG", "backImageUrl:  $backImageUrl ")

                            frontImage = frontImageUrl
                            backImage = backImageUrl
                            // Do something with the download URLs
                        }

                        imageUploadCompleted = 1


                    }
                }
                    .addOnFailureListener { e ->
                        // Error, Image not uploaded
                        Log.e("TAG", "Failed ${e.message}")
                        progressDialog.dismiss()
                        Toast.makeText(this, "Images Uploading Fail", Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener { taskSnapshot ->
                        // Progress Listener for loading percentage on the dialog box
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded ${progress.toInt()}%")
                    }

                uploadTasks.add(uploadTask)
            }


        }
    }


}