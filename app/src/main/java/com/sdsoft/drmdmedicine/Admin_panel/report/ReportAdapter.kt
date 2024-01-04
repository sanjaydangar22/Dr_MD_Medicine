package com.sdsoft.drmdmedicine.Admin_panel.report

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

class ReportAdapter(var context: Context, var itemClick: (ReportModelClass) -> Unit) :
    RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {

    var reportList = ArrayList<ReportModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var reportImage: ImageView = itemView.findViewById(R.id.imgMedicineImage)
        var reportName: TextView = itemView.findViewById(R.id.txtMedicineName)
        var cdreport: CardView = itemView.findViewById(R.id.cdMedicine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rcv_medicine_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.reportName.text = reportList[position].reportName

        Glide.with(context).load(reportList[position].reportImage)
            .placeholder(R.drawable.ic_image).into(holder.reportImage)

        holder.cdreport.setOnClickListener {
            itemClick.invoke(reportList[position])
        }
    }

    fun updateList(patientList: ArrayList<ReportModelClass>) {
        this.reportList = patientList
        notifyDataSetChanged()
    }

}