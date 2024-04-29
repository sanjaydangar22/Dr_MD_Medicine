package com.sdsoft.drmdmedicine.Admin_panel.adapter_class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientModelClass
import com.sdsoft.drmdmedicine.R

class PatientListAdapter(
    var context: Context,
    var itemClick: (PatientModelClass) -> Unit,
    var itemEdit: (String) -> Unit,
    var itemDelete: (String) -> Unit
) :
    RecyclerView.Adapter<PatientListAdapter.MyViewHolder>() {

    var patientList = ArrayList<PatientModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientImage: ImageView = itemView.findViewById(R.id.imgPatientImage)
        var patientName: TextView = itemView.findViewById(R.id.txtPatientName)
        var patientMobileNo: TextView = itemView.findViewById(R.id.txtPatientMobileNo)
        var patientVillage: TextView = itemView.findViewById(R.id.txtPatientVillage)
        var patientAge: TextView = itemView.findViewById(R.id.txtPatientAge)
        var linPatient: LinearLayout = itemView.findViewById(R.id.linPatient)
        var edit: ImageView = itemView.findViewById(R.id.imgEdit)
        var delete: ImageView = itemView.findViewById(R.id.imgDelete)
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
        holder.patientMobileNo.text = patientList[position].patientMobileNo
        holder.patientVillage.text = patientList[position].patientVillage
        holder.patientAge.text = patientList[position].patientAge

        Glide.with(context).load(patientList[position].patientImage)
            .placeholder(R.drawable.user_icon).into(holder.patientImage)

        holder.linPatient.setOnClickListener {
            itemClick.invoke(patientList[position])
        }

        holder.edit.setOnClickListener {
            itemEdit.invoke(patientList[position].patientUid!!)
        }
        holder.delete.setOnClickListener {
            itemDelete.invoke(patientList[position].patientUid!!)
        }
    }

    fun updateList(patientList: ArrayList<PatientModelClass>) {
        this.patientList = patientList
        notifyDataSetChanged()
    }

}