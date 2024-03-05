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

class AddAppointmentsListAdapter(
    var context: Context,
    var listType: String,
    var itemClick: (PatientModelClass) -> Unit
) : RecyclerView.Adapter<AddAppointmentsListAdapter.MyViewHolder>() {

    var patientList = ArrayList<PatientModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientImage: ImageView = itemView.findViewById(R.id.imgPatientImage)
        var patientName: TextView = itemView.findViewById(R.id.txtPatientName)
        var patientMobileNo: TextView = itemView.findViewById(R.id.txtPatientMobileNo)
        var patientVillage: TextView = itemView.findViewById(R.id.txtPatientVillage)
        var patientAge: TextView = itemView.findViewById(R.id.txtPatientAge)
        var linPatient: LinearLayout = itemView.findViewById(R.id.linPatient)
        var linAppointmentNo: LinearLayout = itemView.findViewById(R.id.linAppointmentNo)
        var txtAppointmentsNo: TextView = itemView.findViewById(R.id.txtAppointmentsNo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rcv_add_appointments_list, parent, false)
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

        if (listType == "AppointmentList") {
            holder.linAppointmentNo.visibility = View.VISIBLE
            holder.txtAppointmentsNo.text = patientList[position].appointmentsNumber.toString()
        } else {
            holder.linAppointmentNo.visibility = View.GONE
        }
    }

    fun updateList(updatedList: ArrayList<PatientModelClass>) {
        this.patientList = if (listType == "AppointmentList") {
            updatedList.sortedBy { it.appointmentsNumber }
                .filter { it.appointmentsNumber in 1..updatedList.size } as ArrayList<PatientModelClass>
        } else {
            updatedList
        }
        notifyDataSetChanged()
    }
}
