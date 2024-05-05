package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityAdminLoginBinding
import com.sdsoft.drmdmedicine.databinding.DialogRecoverPasswordBinding
import com.sdsoft.drmdmedicine.databinding.ImageSelctedDialogBinding

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

                        if (email == "dangarmahipatsinh11@gmail.com") {
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
        adminLoginBinding.txtForgotPass.setOnClickListener {

            passwordRecoverDialog()

        }
    }

    fun passwordRecoverDialog(){
        val dialog = Dialog(this)
        val dialogBinding: DialogRecoverPasswordBinding =
            DialogRecoverPasswordBinding.inflate(
                layoutInflater
            )
        dialog.setContentView(dialogBinding.root)


        //Recover
        dialogBinding.btnRecover.setOnClickListener {
            var email = dialogBinding.edtEmail.text.toString().trim()
            beginRecovery(email)
            dialog.dismiss()
        }

        //cancel
        dialogBinding.btnCansel.setOnClickListener { dialog.dismiss() }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun beginRecovery(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Email In Password Sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

    }

}