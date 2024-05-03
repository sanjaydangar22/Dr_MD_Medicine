package com.sdsoft.drmdmedicine.Admin_panel.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsoft.drmdmedicine.Admin_panel.Disease.DiseaseActivity
import com.sdsoft.drmdmedicine.Admin_panel.activity.AppointmentsActivity
import com.sdsoft.drmdmedicine.Admin_panel.activity.CompletedAppointmentsActivity
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.databinding.FragmentAdminHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AdminHomeFragment : Fragment() {
    lateinit var binding: FragmentAdminHomeBinding
    lateinit var mDbRef: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminHomeBinding.inflate(layoutInflater, container, false)
        mDbRef = FirebaseDatabase.getInstance().reference
        initView()
        return binding.root
    }

    private fun initView() {

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Get the current date
        val currentDate = Calendar.getInstance()
        // Format and set the current date in the first TextView
        val formattedCurrentDate = dateFormat.format(currentDate.time)

        mDbRef.child("AppointmentList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        var data = i.getValue(PatientModelClass::class.java)
                        var appointmentDate = data!!.timestamp

                        if (appointmentDate != formattedCurrentDate) {
                            mDbRef.child("AppointmentNumber").setValue(0)
                            mDbRef.child("AppointmentList").removeValue()
                            mDbRef.child("AppointmentCompletedList").removeValue()

                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        binding.cdNewAppointments.setOnClickListener {
            var i = Intent(requireContext(), AppointmentsActivity::class.java)
            startActivity(i)
        }
        binding.cdAppointmentsCompleted.setOnClickListener {
            var i = Intent(requireContext(), CompletedAppointmentsActivity::class.java)
            startActivity(i)
        }
        binding.cdDiseaseList.setOnClickListener {
            var i = Intent(requireContext(), DiseaseActivity::class.java)
            startActivity(i)
        }

    }


}