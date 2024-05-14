package com.sdsoft.drmdmedicine.Admin_panel.Disease

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.DiseaseListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ModelClass
import com.sdsoft.drmdmedicine.BaseActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.ActivityDiseaseBinding
import com.sdsoft.drmdmedicine.databinding.DialogAddNewItemBinding
import java.util.UUID

class DiseaseActivity : BaseActivity(R.layout.activity_disease) {

    lateinit var binding: ActivityDiseaseBinding
    var diseaseList = ArrayList<ModelClass>()
    lateinit var adapter: DiseaseListAdapter

    lateinit var dialog: Dialog
    lateinit var dialogBinding: DialogAddNewItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = FirebaseDatabase.getInstance().reference
        progressBarDialog = ProgressBarDialog(this)


        diseaseDialogFun()
        initView()
    }

    private fun diseaseDialogFun() {
        dialog = Dialog(this)
        dialogBinding = DialogAddNewItemBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        dialogBinding.imgClose.setOnClickListener {
            dialog.dismiss()
            dialogBinding.edtName.setText("")
        }


    }

    private fun initView() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
//search disease
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {

                    searchItems(newText)

                }
                return true
            }
        })
        progressBarDialog.show()
//       disease List adapter
        adapter = DiseaseListAdapter(this) {
            editAndDeleteDiseaseFun(it)

        }
        var manger = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rcvDiseaseList.layoutManager = manger
        binding.rcvDiseaseList.adapter = adapter

        //        diseaseList show in recycler view
        mDbRef.child("DiseaseList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    diseaseList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(ModelClass::class.java)

                        data?.let { it1 -> diseaseList.add(it1) }
                    }

                    if (diseaseList.isEmpty()) {
                        binding.linNoDataFound.visibility = View.VISIBLE
                    } else if (diseaseList.isNotEmpty()) {
                        binding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(diseaseList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        binding.imgAddDisease.setOnClickListener {
            dialogBinding.btnSubmit.text = "Submit"
            dialogBinding.cdDelete.visibility = View.GONE
            dialog.show()
            dialogBinding.btnSubmit.setOnClickListener {
                var diseaseName = dialogBinding.edtName.text.toString()

                if (diseaseName.isEmpty()) {
                    Toast.makeText(this, "Please Enter Disease Name", Toast.LENGTH_SHORT).show()
                } else {
                    addDiseaseFun(diseaseName)

                }
            }

        }
    }


    private fun addDiseaseFun(diseaseName: String) {


        var diseaseUid = UUID.randomUUID().toString()
        progressBarDialog.show()
        mDbRef.child("DiseaseList").child(diseaseUid)
            .setValue(ModelClass(diseaseName, diseaseUid))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Record Save Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    dialogBinding.edtName.setText("")
                    progressBarDialog.dismiss()
                    dialog.dismiss()
                }
            }.addOnFailureListener {
                progressBarDialog.dismiss()
                dialog.dismiss()

            }
    }

    private fun editAndDeleteDiseaseFun(model: ModelClass) {
        dialog.show()
        dialogBinding.txtDialogTitle.text = "Update Disease"
        dialogBinding.edtName.setText(model.name)
        dialogBinding.cdDelete.visibility = View.VISIBLE


        dialogBinding.btnSubmit.text = "Update"

        dialogBinding.btnSubmit.setOnClickListener {
            var diseaseName = dialogBinding.edtName.text.toString()

            if (diseaseName.isEmpty()) {
                Toast.makeText(this, "Please Enter Disease Name", Toast.LENGTH_SHORT).show()
            } else {


                progressBarDialog.show()
                mDbRef.child("DiseaseList").child(model.uid!!)
                    .setValue(ModelClass(diseaseName, model.uid!!))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Record Save Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()


                            progressBarDialog.dismiss()
                            dialog.dismiss()
                        }
                    }.addOnFailureListener {
                        progressBarDialog.dismiss()

                    }


            }
        }

        dialogBinding.cdDelete.setOnClickListener {

            progressBarDialog.show()
            mDbRef.child("DiseaseList").child(model.uid!!)
                .removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        Toast.makeText(this, "Record Deleted Successfully", Toast.LENGTH_SHORT)
                            .show()
                        progressBarDialog.dismiss()
                        dialog.dismiss()

                    }
                }.addOnFailureListener {

                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    //  search view function
    private fun searchItems(query: String) {
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
                        binding.linNoDataFound.visibility = View.VISIBLE
                    } else {
                        binding.linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }


}
