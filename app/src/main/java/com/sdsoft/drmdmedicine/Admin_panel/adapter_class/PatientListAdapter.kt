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
        var patientImage: ImageView = itemView.findViewById(R.id.imgMedicineImage)
        var patientName: TextView = itemView.findViewById(R.id.txtMedicineName)
        var cdpatient: CardView = itemView.findViewById(R.id.cdMedicine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rcv_medicine_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.patientName.text = patientList[position].patientName

        Glide.with(context).load(patientList[position].patientImage)
            .placeholder(R.drawable.ic_image).into(holder.patientImage)

        holder.cdpatient.setOnClickListener {
            itemClick.invoke(patientList[position])
        }
    }

    fun updateList(patientList: ArrayList<PatientModelClass>) {
        this.patientList = patientList
        notifyDataSetChanged()
    }

}