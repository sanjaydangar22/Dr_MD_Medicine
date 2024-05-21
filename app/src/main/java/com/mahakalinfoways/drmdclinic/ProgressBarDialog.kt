package com.mahakalinfoways.drmdclinic

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.LinearLayout
import com.mahakalinfoways.drmdclinic.databinding.DialogProgressBarBinding

class ProgressBarDialog(context: Context) : Dialog(context) {
    init {
        setCancelable(false)
    }

    companion object {

        lateinit var progressBarBinding: DialogProgressBarBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the window background to transparent
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater)
        setContentView(progressBarBinding.root)

    }


}