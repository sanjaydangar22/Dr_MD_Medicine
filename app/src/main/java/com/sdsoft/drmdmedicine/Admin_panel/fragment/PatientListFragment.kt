package com.sdsoft.drmdmedicine.Admin_panel.fragment

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
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
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
import com.sdsoft.drmdmedicine.Admin_panel.activity.AddPatientActivity
import com.sdsoft.drmdmedicine.Admin_panel.activity.PatientDataViewActivity
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.PatientListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.databinding.DeleteDialogBinding
import com.sdsoft.drmdmedicine.databinding.FragmentPatientListBinding

class PatientListFragment : Fragment() {
    lateinit var patientBinding: FragmentPatientListBinding
    var patientList = ArrayList<PatientModelClass>()

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    lateinit var adapter: PatientListAdapter

    lateinit var progressBarDialog: ProgressBarDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        patientBinding = FragmentPatientListBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        progressBarDialog = ProgressBarDialog(requireContext())

        mDbRef = FirebaseDatabase.getInstance().reference
        // Initialize Firebase Auth
        auth = Firebase.auth
        // get the Firebase  storage reference
        storageReference = FirebaseStorage.getInstance().reference

        initView()
        return patientBinding.root
    }

    private fun initView() {
//        patient List adapter
        adapter = PatientListAdapter(requireContext(), {
            //view data
            var i = Intent(requireContext(), PatientDataViewActivity::class.java)
            i.putExtra("patientUid", it.patientUid)
            requireContext().startActivity(i)
        }, { patientUid ->
            //edit data
            var i = Intent(requireContext(), AddPatientActivity::class.java)
            i.putExtra("patientUid", patientUid)
            i.putExtra("itemUpdate", true)
            requireContext().startActivity(i)
        }, { patientUid ->
            //delete data
            deleteRecordFromDatabase(patientUid)

        })
        var manger = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        patientBinding.rcvPatientList.layoutManager = manger
        patientBinding.rcvPatientList.adapter = adapter

        //progress dialog show
        progressBarDialog.show()


//search patient
        patientBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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


//        add new patient
        patientBinding.imgAddPatient.setOnClickListener {
            var i = Intent(this.activity, AddPatientActivity::class.java)
            requireContext().startActivity(i)
        }


//        patient list show in recycler view
        mDbRef.child("PatientList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    patientList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(PatientModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange: " + data?.patientName + data?.patientAge
                        )
                        data?.let { it1 -> patientList.add(it1) }
                    }

                    if (patientList.isEmpty()) {
                        patientBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (patientList.isNotEmpty()) {
                        patientBinding.linNoDataFound.visibility = View.GONE
                    }
                    adapter.updateList(patientList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }


    //    search view function
    private fun searchItems(query: String) {
        mDbRef.child("PatientList").orderByChild("patientName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<PatientModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(PatientModelClass::class.java)
                        item?.let { searchItems.add(it) }
                    }


                    adapter.updateList(searchItems)

                    if (searchItems.isEmpty()) {
                        patientBinding.linNoDataFound.visibility = View.VISIBLE
                    } else if (searchItems.isNotEmpty()) {
                        patientBinding.linNoDataFound.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }

    private fun deleteRecordFromDatabase(patientUid: String) {

        var deleteDialog = Dialog(requireContext())

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(requireContext(), "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("PatientList").child(patientUid)
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

                    }
                }.addOnFailureListener {

                    Toast.makeText(requireContext(), "fail", Toast.LENGTH_SHORT)
                        .show()
                }

            deleteDialog.dismiss()
        }

        deleteDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

    }

}