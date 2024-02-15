package com.sdsoft.drmdmedicine.Admin_panel.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sdsoft.drmdmedicine.R
import com.sdsoft.drmdmedicine.databinding.FragmentAdminHomeBinding


class AdminHomeFragment : Fragment() {
    lateinit var binding: FragmentAdminHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminHomeBinding.inflate(layoutInflater, container, false)

        initView()
        return  binding.root
    }

    private fun initView() {


    }


}