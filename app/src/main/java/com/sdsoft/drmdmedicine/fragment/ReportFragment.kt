package com.sdsoft.drmdmedicine.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.FragmentReportBinding

class ReportFragment : Fragment() {
lateinit var binding: FragmentReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentReportBinding.inflate(layoutInflater,container,false)

        initView()
        return binding.root
    }

    private fun initView() {
        TODO("Not yet implemented")
    }


}