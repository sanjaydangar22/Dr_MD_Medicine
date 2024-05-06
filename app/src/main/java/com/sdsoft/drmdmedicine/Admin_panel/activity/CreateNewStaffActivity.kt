package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.sdsoft.drmdmedicine.Admin_panel.model_class.StaffModelClass
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityCreateNewStaffBinding
import java.util.UUID

class CreateNewStaffActivity : BaseActivity(R.layout.activity_create_new_staff) {
    lateinit var binding: ActivityCreateNewStaffBinding
    var flag = 0

    private var isPasswordVisible = false
    var staffUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        // Initialize Firebase Auth
        auth = Firebase.auth

        progressBarDialog = ProgressBarDialog(this)
        passwordToggle()
        initView()
    }

    private fun passwordToggle() {
        // Hide the password
        binding.edtPassword.transformationMethod =
            PasswordTransformationMethod.getInstance()
        binding.imgPasswordToggle.setOnClickListener {
            if (isPasswordVisible) {
                // Hide the password
                binding.edtPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                isPasswordVisible = false
                binding.imgPasswordToggle.setImageResource(R.drawable.eye_hidde)
            } else {
                // Show the password
                binding.edtPassword.transformationMethod = null
                isPasswordVisible = true
                binding.imgPasswordToggle.setImageResource(R.drawable.eye_show)
            }

            // Move the cursor to the end of the text
            binding.edtPassword.setSelection(binding.edtPassword.text.length)
        }
    }

    private fun initView() {

        if (intent != null && intent.hasExtra("itemUpdate")) {  // data update key access this class

            flag = 1

            staffUid = intent.getStringExtra("staffUid")   // key set  variable

            mDbRef.child("StaffList").child(staffUid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val patientItem = snapshot.getValue(StaffModelClass::class.java)
                            if (patientItem != null) {
                                // User data retrieved successfully
                                val staffUid = patientItem.staffUid
                                val staffName = patientItem.staffName
                                val staffMobileNumber = patientItem.staffMobileNumber
                                val staffEmail = patientItem.staffEmail
                                val staffPassword = patientItem.staffPassword

                                binding.edStaffName.setText(staffName.toString())
                                binding.edtStaffMobileNo.setText(staffMobileNumber.toString())
                                binding.edtEmail.setText(staffEmail.toString())
                                binding.edtPassword.setText(staffPassword.toString())


                                //change page title
                                binding.txtTitle.text = "Staff Edit"
                                //change button name
                                binding.txtSubmitName.text = "Update"


                            }
                        } else {
                            // User data does not exist
                            Toast.makeText(
                                this@CreateNewStaffActivity,
                                "No Staff",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
        binding.btnSubmit.setOnClickListener {

            var staffName = binding.edStaffName.text.toString()
            var staffMobileNumber = binding.edtStaffMobileNo.text.toString()
            var staffEmail = binding.edtEmail.text.toString()
            var staffPassword = binding.edtPassword.text.toString()


          if (staffEmail.isEmpty()) {
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(staffEmail).matches()) {
                Toast.makeText(this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show()

            } else if (staffPassword.isEmpty()) {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()

            } else if (staffPassword!!.length < 6) {
                Toast.makeText(this, "Minimum 6 Character Password", Toast.LENGTH_SHORT).show()

            } else {
                progressBarDialog.show()
                if (flag == 1) {
                    mDbRef.child("StaffList").child(staffUid!!).setValue(
                        StaffModelClass(
                            staffName,
                            staffMobileNumber,
                            staffEmail,
                            staffPassword,
                            staffUid!!
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
                } else {

                    var staffUid = UUID.randomUUID().toString()
                    auth.createUserWithEmailAndPassword(staffEmail, staffPassword)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                addStaffToDatabase(
                                    staffName,
                                    staffMobileNumber,
                                    staffEmail,
                                    staffPassword,
                                    staffUid
                                )
                                Toast.makeText(this, "Account Create Success", Toast.LENGTH_SHORT)
                                    .show()

                                progressBarDialog.dismiss()
                                finish()
                            }
                        }.addOnFailureListener {

                            Toast.makeText(this, "Account Create Fail", Toast.LENGTH_SHORT).show()
                            progressBarDialog.dismiss()
                        }

                }
            }
        }
    }

    private fun addStaffToDatabase(
        staffName: String,
        staffMobileNumber: String,
        staffEmail: String,
        staffPassword: String,
        staffUid: String
    ) {
        mDbRef.child("StaffList").child(staffUid).setValue(
            StaffModelClass(
                staffName,
                staffMobileNumber,
                staffEmail,
                staffPassword,
                staffUid
            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    this,
                    "Record Save Successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }.addOnFailureListener {


        }
    }
}
