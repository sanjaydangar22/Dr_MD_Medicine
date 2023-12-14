package com.sdsoft.drmdmedicine.Admin_panel

import android.app.Dialog
import android.app.ProgressDialog
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
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sdsoft.drmdmedicine.databinding.ActivityAddMedicineBinding
import com.sdsoft.drmdmedicine.databinding.ImageSelctedDialogBinding
import java.util.UUID

class AddMedicineActivity : AppCompatActivity() {
    lateinit var addMedicineBinding: ActivityAddMedicineBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    lateinit var frontImagePath: Uri
    lateinit var backImagePath: Uri

    var imageUploadCompleted = 0
    var selectedImage = 0
    var frontImage: String? = null
    var backImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addMedicineBinding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(addMedicineBinding.root)

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
    }

    private fun initView() {
        addMedicineBinding.imgAddFrontImage.setOnClickListener {

            selectedImage = 1
            selectedImageDialog()
        }
        addMedicineBinding.imgAddBackImage.setOnClickListener {

            selectedImage = 2
            selectedImageDialog()
        }

        addMedicineBinding.cdUploadImage.setOnClickListener {
            imageUpload()
        }
        addMedicineBinding.cdSave.setOnClickListener {
            var medicineCompanyName = addMedicineBinding.edtMedicineCompanyName.text.toString()
            var medicineName = addMedicineBinding.edtMedicineName.text.toString()
            var medicineUse = addMedicineBinding.edtMedicineName.text.toString()

            if (medicineCompanyName.isEmpty()) {
                Toast.makeText(this, "Medicine Company name is empty", Toast.LENGTH_SHORT).show()
            } else if (medicineName.isEmpty()) {
                Toast.makeText(this, "Medicine  name is empty", Toast.LENGTH_SHORT).show()
            } else if (medicineUse.isEmpty()) {
                Toast.makeText(this, "Medicine use is empty", Toast.LENGTH_SHORT).show()
            } else {
                mDbRef.child("MedicineList").setValue(
                    MedicineModelClass(
                        frontImage!!,
                        backImage!!,
                        medicineCompanyName,
                        medicineName,
                        medicineUse
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
                    }
                }.addOnFailureListener {
                    Log.e("TAG", "fail: " + it.message)

                }

                if (imageUploadCompleted == 1) {

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

    //camera
    var camera_Launcher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val b = data!!.extras!!["data"] as Bitmap?

                if (selectedImage == 1) {
                    frontImagePath = data.data!!
                    addMedicineBinding.imgFrontImage.setImageBitmap(b)
                } else if (selectedImage == 2) {
                    backImagePath = data.data!!
                    addMedicineBinding.imgBackImage.setImageBitmap(b)
                }
            }

        })

    //gallery
    var gallery_Launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode === RESULT_OK) {
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

            val imagesPath = listOf(frontImagePath, backImagePath)

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            progressDialog.setCancelable(false)

            // Defining the child of storageReference
            val ref = storageReference.child("images/" + UUID.randomUUID().toString())

            val uploadTasks = mutableListOf<UploadTask>()

            // Upload each image in the list
            imagesPath.forEachIndexed { index, imageUri ->
                // Adding listeners on upload or failure of the image
                val uploadTask = ref.child("image${index + 1}").putFile(imageUri)

                uploadTask.addOnSuccessListener {
                    // Image uploaded successfully
                    // You can get the download URL or perform other operations here
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Log.e("TAG", "uploadImage: $imageUrl")
                        // Handle the download URL as needed
                        progressDialog.dismiss()
                        Toast.makeText(this, "Images Uploaded!!", Toast.LENGTH_SHORT).show()
                        imageUploadCompleted = 1
                    }
                }
                    .addOnFailureListener { e ->
                        // Error, Image not uploaded
                        Log.e("TAG", "Failed ${e.message}")
                    }
                    .addOnProgressListener { taskSnapshot ->
                        // Progress Listener for loading percentage on the dialog box
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded ${progress.toInt()}%")
                    }

                uploadTasks.add(uploadTask)
            }

//            // Wait for all tasks to complete
//            Tasks.whenAllSuccess(*uploadTasks.toTypedArray())
//                .addOnCompleteListener {
//                    progressDialog.dismiss()
//                    Toast.makeText(this, "Images Uploaded!!", Toast.LENGTH_SHORT).show()
//                    imageUploadCompleted = 1
//                }
//                .addOnFailureListener { e ->
//                    progressDialog.dismiss()
//                    Toast.makeText(this, "Failed ${e.message}", Toast.LENGTH_SHORT).show()
//                }
        }
    }
}