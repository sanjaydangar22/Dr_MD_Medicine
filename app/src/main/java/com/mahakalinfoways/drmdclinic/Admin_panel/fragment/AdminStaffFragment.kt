package com.mahakalinfoways.drmdclinic.Admin_panel.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahakalinfoways.drmdclinic.Admin_panel.activity.CreateNewStaffActivity
import com.mahakalinfoways.drmdclinic.Admin_panel.adapter_class.StaffListAdapter
import com.mahakalinfoways.drmdclinic.Admin_panel.model_class.StaffModelClass
import com.mahakalinfoways.drmdclinic.ProgressBarDialog
import com.mahakalinfoways.drmdclinic.databinding.DeleteDialogBinding
import com.mahakalinfoways.drmdclinic.databinding.FragmentAdminStaffBinding


class AdminStaffFragment : Fragment() {
    lateinit var binding: FragmentAdminStaffBinding
    lateinit var progressBarDialog: ProgressBarDialog

    lateinit var mDbRef: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminStaffBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        progressBarDialog = ProgressBarDialog(requireContext())

        mDbRef = FirebaseDatabase.getInstance().reference
        initView()

        return binding.root
    }

    private fun initView() {

        binding.cdNewStaffAccount.setOnClickListener {
            var i = Intent(requireContext(), CreateNewStaffActivity::class.java)
            startActivity(i)
        }
        //        Staff List adapter
        var adapter = StaffListAdapter(requireContext(), { staffUid ->
            //edit data
            var i = Intent(requireContext(), CreateNewStaffActivity::class.java)
            i.putExtra("staffUid", staffUid)
            i.putExtra("itemUpdate", true)
            requireContext().startActivity(i)
        }, { staffUid ->
            //delete data
            deleteRecordFromDatabase(staffUid)

        })
        var manger = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.rcvStaffList.layoutManager = manger
        binding.rcvStaffList.adapter = adapter
        val staffList = ArrayList<StaffModelClass>()

        mDbRef.child("StaffList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                staffList.clear()
                for (i in snapshot.children) {
                    val data = i.getValue(StaffModelClass::class.java)
                    data?.let { staffList.add(it) }
                }
                if (staffList.isEmpty()) {
                    binding.linStaffNotFound.visibility = View.VISIBLE
                } else if (staffList.isNotEmpty()) {
                    binding.linStaffNotFound.visibility = View.GONE
                }
                adapter.updateList(staffList)
                progressBarDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    requireContext(),
                    "Database Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


    }
    private fun deleteRecordFromDatabase(staffUid: String) {

        var deleteDialog = Dialog(requireContext())

        var dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(requireContext(), "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            mDbRef.child("StaffList").child(staffUid)
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