package com.mahakalinfoways.drmdclinic.Admin_panel.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mahakalinfoways.drmdclinic.Admin_panel.Disease.DiseaseActivity
import com.mahakalinfoways.drmdclinic.Admin_panel.activity.AppointmentsActivity
import com.mahakalinfoways.drmdclinic.Admin_panel.activity.CompletedAppointmentsActivity
import com.mahakalinfoways.drmdclinic.Admin_panel.activity.PatientHistoryActivity
import com.mahakalinfoways.drmdclinic.databinding.FragmentAdminHomeBinding


class AdminHomeFragment : Fragment() {
    lateinit var binding: FragmentAdminHomeBinding
    lateinit var mDbRef: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminHomeBinding.inflate(layoutInflater, container, false)
        mDbRef = FirebaseDatabase.getInstance().reference
        initView()
        return binding.root
    }

    private fun initView() {


        binding.cdNewAppointments.setOnClickListener {
            var i = Intent(requireContext(), AppointmentsActivity::class.java)
            i.putExtra("userType","Doctor")
            startActivity(i)
        }
        binding.cdAppointmentsCompleted.setOnClickListener {
            var i = Intent(requireContext(), CompletedAppointmentsActivity::class.java)
            i.putExtra("userType","Doctor")
            startActivity(i)
        }
        binding.cdDiseaseList.setOnClickListener {
            var i = Intent(requireContext(), DiseaseActivity::class.java)
            startActivity(i)
        }
        binding.cdPatientHistory.setOnClickListener {
            var i = Intent(requireContext(), PatientHistoryActivity::class.java)
            startActivity(i)
        }
    }


}