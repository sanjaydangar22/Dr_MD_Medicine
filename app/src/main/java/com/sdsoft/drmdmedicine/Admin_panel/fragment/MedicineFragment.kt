package com.sdsoft.drmdmedicine.Admin_panel.fragment

import android.R
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.MedicineListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.databinding.DialogAddNewItemBinding
import com.sdsoft.drmdmedicine.databinding.FragmentMedicineBinding
import java.util.UUID

class MedicineFragment : Fragment() {

    lateinit var medicineBinding: FragmentMedicineBinding
    var medicineList = ArrayList<ModelClass>()

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    lateinit var adapter: MedicineListAdapter
    lateinit var dialog: Dialog
    lateinit var dialogBinding: DialogAddNewItemBinding
    lateinit var progressBarDialog: ProgressBarDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        medicineBinding = FragmentMedicineBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        progressBarDialog = ProgressBarDialog(requireContext())

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference
        dialogFun()
        initView()
        return medicineBinding.root
    }

    private fun dialogFun() {
        dialog = Dialog(requireContext())
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
//        medicine List adapter
        adapter = MedicineListAdapter(requireContext()) {

            editAndDeleteMedicineFun(it)
        }
        var manger = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        medicineBinding.rcvMedicineList.layoutManager = manger
        medicineBinding.rcvMedicineList.adapter = adapter

        //progress dialog show
        progressBarDialog.show()


//search medicine
        medicineBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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


//        add new medicine
        medicineBinding.imgAddMedicine.setOnClickListener {
            dialogBinding.txtDialogTitle.text = "Add Medicine"
            dialogBinding.btnSubmit.text = "Submit"
            dialogBinding.cdDelete.visibility = View.GONE
            dialog.show()
            dialogBinding.btnSubmit.setOnClickListener {
                var diseaseName = dialogBinding.edtName.text.toString()

                if (diseaseName.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Medicine Name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    addMedicineFun(diseaseName)

                }
            }
        }


//        medicine list show in recycler view
        mDbRef.child("MedicineList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicineList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(ModelClass::class.java)

                        data?.let { it1 -> medicineList.add(it1) }
                    }

                    if (medicineList.isEmpty()) {
                        medicineBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (medicineList.isNotEmpty()) {
                        medicineBinding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(medicineList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }

    private fun addMedicineFun(name: String) {


        var uid = UUID.randomUUID().toString()
        progressBarDialog.show()
        mDbRef.child("MedicineList").child(uid)
            .setValue(ModelClass(name, uid))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
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

    private fun editAndDeleteMedicineFun(model: ModelClass) {
        dialog.show()
        dialogBinding.txtDialogTitle.text = "Update Medicine"
        dialogBinding.edtName.setText(model.name)
        dialogBinding.cdDelete.visibility = View.VISIBLE


        dialogBinding.btnSubmit.text = "Update"

        dialogBinding.btnSubmit.setOnClickListener {
            var diseaseName = dialogBinding.edtName.text.toString()

            if (diseaseName.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter Medicine Name", Toast.LENGTH_SHORT)
                    .show()
            } else {


                progressBarDialog.show()
                mDbRef.child("MedicineList").child(model.uid!!)
                    .setValue(ModelClass(diseaseName, model.uid!!))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
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
            mDbRef.child("MedicineList").child(model.uid!!)
                .removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        Toast.makeText(
                            requireContext(),
                            "Record Deleted Successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        progressBarDialog.dismiss()
                        dialog.dismiss()

                    }
                }.addOnFailureListener {

                    Toast.makeText(requireContext(), "fail", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    //    search view function
    private fun searchItems(query: String) {


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
                        medicineBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (searchItems.isNotEmpty()) {
                        medicineBinding.linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }


}