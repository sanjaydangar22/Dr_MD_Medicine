package com.sdsoft.drmdmedicine.Admin_panel.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
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
import com.sdsoft.drmdmedicine.Admin_panel.AddMedicineActivity
import com.sdsoft.drmdmedicine.Admin_panel.MedicineListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.MedicineModelClass
import com.sdsoft.drmdmedicine.Admin_panel.MedicineViewActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.FragmentMedicineBinding

class MedicineFragment : Fragment() {

    lateinit var medicineBinding: FragmentMedicineBinding
    var medicineList = ArrayList<MedicineModelClass>()

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

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
        initView()
        return medicineBinding.root
    }

    private fun initView() {
        medicineBinding.imgAddMedicine.setOnClickListener {
            var i = Intent(this.activity, AddMedicineActivity::class.java)
            requireContext().startActivity(i)
        }
        var adapter = MedicineListAdapter(requireContext()){

            var i=Intent(requireContext(),MedicineViewActivity::class.java)
            i.putExtra("medicineUid",it.medicineUid)
            requireContext().startActivity(i)
        }
        var manger = GridLayoutManager(context, 2)

        medicineBinding.rcvMedicineList.layoutManager = manger
        medicineBinding.rcvMedicineList.adapter = adapter

        progressBarDialog.show()

        mDbRef.child("MedicineList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicineList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(MedicineModelClass::class.java)
                        Log.e("TAG", "onDataChange: " + data?.medicineName + data?.medicineCompanyName)
                        data?.let { it1 -> medicineList.add(it1) }
                    }
                    adapter.updateList(medicineList)
                    progressBarDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })









    }


}