package com.sdsoft.drmdmedicine.Admin_panel.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.sdsoft.drmdmedicine.Admin_panel.AddMedicineActivity
import com.sdsoft.drmdmedicine.Admin_panel.MedicineListAdapter
import com.sdsoft.drmdmedicine.Admin_panel.MedicineModelClass
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.FragmentMedicineBinding

class MedicineFragment : Fragment() {

    lateinit var medicineBinding: FragmentMedicineBinding
    var medicineList = ArrayList<MedicineModelClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        medicineBinding = FragmentMedicineBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment

        initView()
        return medicineBinding.root
    }

    private fun initView() {
        medicineBinding.imgAddMedicine.setOnClickListener {
            var i = Intent(this.activity, AddMedicineActivity::class.java)
            requireContext().startActivity(i)
        }

        var adapter = MedicineListAdapter(requireContext())
        var manger = GridLayoutManager(context, 2)

        medicineBinding.rcvMedicineList.layoutManager = manger
        medicineBinding.rcvMedicineList.adapter = adapter

        adapter.updateList(medicineList)

    }


}