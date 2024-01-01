package com.sdsoft.drmdmedicine.Admin_panel.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.sdsoft.drmdmedicine.Admin_panel.activity.AddMedicineActivity
import com.sdsoft.drmdmedicine.Admin_panel.adapter_class.MedicineListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.Admin_panel.activity.MedicineViewActivity
import com.sdsoft.drmdmedicine.ProgressBarDialog
import com.sdsoft.drmdmedicine.databinding.FragmentMedicineBinding

class MedicineFragment : Fragment() {

    lateinit var medicineBinding: FragmentMedicineBinding
    var medicineList = ArrayList<MedicineModelClass>()

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    lateinit var storageReference: StorageReference

    lateinit var adapter: MedicineListAdapter

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
//        medicine List adapter
        adapter = MedicineListAdapter(requireContext()) {

            var i = Intent(requireContext(), MedicineViewActivity::class.java)
            i.putExtra("medicineUid", it.medicineUid)
            requireContext().startActivity(i)
        }
        var manger = GridLayoutManager(context, 2)

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
            var i = Intent(this.activity, AddMedicineActivity::class.java)
            requireContext().startActivity(i)
        }


//        medicine list show in recycler view
        mDbRef.child("MedicineList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    medicineList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(MedicineModelClass::class.java)
                        Log.e(
                            "TAG",
                            "onDataChange: " + data?.medicineName + data?.medicineCompanyName
                        )
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


//    search view function
    private fun searchItems(query: String) {
        mDbRef.child("MedicineList").orderByChild("medicineName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val searchItems = ArrayList<MedicineModelClass>()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(MedicineModelClass::class.java)
                        item?.let { searchItems.add(it) }
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