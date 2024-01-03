package com.sdsoft.drmdmedicine.Admin_panel.adapter_class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdsoft.drmdmedicine.Admin_panel.model_class.MedicineModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.R

class PatientListAdapter(var context: Context, var itemClick: (PatientModelClass) -> Unit) :
    RecyclerView.Adapter<PatientListAdapter.MyViewHolder>() {

    var patientList = ArrayList<PatientModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientImage: ImageView = itemView.findViewById(R.id.imgPatientImage)
        var patientName: TextView = itemView.findViewById(R.id.txtPatientName)
        var patientAge: TextView = itemView.findViewById(R.id.txtPatientAge)
        var patientGender: TextView = itemView.findViewById(R.id.txtPatientGender)
        var patientMobileNo: TextView = itemView.findViewById(R.id.txtPatientMobileNo)
        var cdpatient: CardView = itemView.findViewById(R.id.cdPatient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rcv_patient_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.patientName.text = patientList[position].patientName
        holder.patientAge.text = patientList[position].patientAge
        holder.patientGender.text = patientList[position].patientGender
        holder.patientMobileNo.text = patientList[position].patientMobileNo

        Glide.with(context).load(patientList[position].patientImage)
            .placeholder(R.drawable.user_icon).into(holder.patientImage)

        holder.cdpatient.setOnClickListener {
            itemClick.invoke(patientList[position])
        }
    }

    fun updateList(patientList: ArrayList<PatientModelClass>) {
        this.patientList = patientList
        notifyDataSetChanged()
    }

}