package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityAdminLoginBinding

class AdminLoginActivity : BaseActivity(R.layout.activity_admin_login) {
    lateinit var adminLoginBinding: ActivityAdminLoginBinding
    lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminLoginBinding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(adminLoginBinding.root)
        auth = Firebase.auth
        progressBarDialog = ProgressBarDialog(this)

        passwordToggle()
        initView()

    }

    private fun passwordToggle() {
        // Hide the password
        adminLoginBinding.edtPassword.transformationMethod =
            PasswordTransformationMethod.getInstance()
        adminLoginBinding.imgPasswordToggle.setOnClickListener {
            if (isPasswordVisible) {
                // Hide the password
                adminLoginBinding.edtPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                isPasswordVisible = false
                adminLoginBinding.imgPasswordToggle.setImageResource(R.drawable.eye_hidde)
            } else {
                // Show the password
                adminLoginBinding.edtPassword.transformationMethod = null
                isPasswordVisible = true
                adminLoginBinding.imgPasswordToggle.setImageResource(R.drawable.eye_show)
            }

            // Move the cursor to the end of the text
            adminLoginBinding.edtPassword.setSelection(adminLoginBinding.edtPassword.text.length)
        }
    }

    private fun initView() {


        adminLoginBinding.btnLogin.setOnClickListener {

            var email = adminLoginBinding.edtEmail.text.toString()
            var password = adminLoginBinding.edtPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "email value is empty. please fill email ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "password value is empty. please fill password ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                progressBarDialog.show()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {

                        if (email == "krishnaclinic@gmail.com") {
                            progressBarDialog.dismiss()

                            var myEdit: SharedPreferences.Editor =
                                doctorsharedPreferences.edit()
                            myEdit.putBoolean("isLogin", true)
                            myEdit.putString("email", email)
                            myEdit.commit()

                            Toast.makeText(this, "Doctor Login Success", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AdminHomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            progressBarDialog.dismiss()
                            Toast.makeText(this, "You are not Doctor", Toast.LENGTH_SHORT).show()
                        }

                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    progressBarDialog.dismiss()
                }
            }
        }
    }
}