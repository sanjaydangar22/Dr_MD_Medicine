package com.sdsoft.drmdmedicine.Admin_panel.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdsoft.drmdmedicine.databinding.ActivityAdminLoginBinding

class AdminLoginActivity : AppCompatActivity() {
    lateinit var adminLoginBinding: ActivityAdminLoginBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminLoginBinding= ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(adminLoginBinding.root)
        auth = Firebase.auth
        initView()
    }

    private fun initView() {
        adminLoginBinding.btnSubmit.setOnClickListener {

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
//                progressDialog.show()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
//                        if (email == "admin@gmail.com") {
//                            progressDialog.dismiss()
                            Toast.makeText(this, "Admin Login Success", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AdminHomeActivity::class.java)
                            startActivity(intent)
                            finish()
//                        } else {
////                            progressDialog.dismiss()
//                            Toast.makeText(this, "You are not Admin", Toast.LENGTH_SHORT).show()
//                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//                    progressDialog.dismiss()
                }
            }
        }
    }
}