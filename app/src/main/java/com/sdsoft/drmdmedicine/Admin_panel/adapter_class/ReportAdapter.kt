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
import com.sdsoft.drmdmedicine.Admin_panel.Patient_data.report.ReportModelClass
import com.sdsoft.drmdmedicine.R

class ReportAdapter(var context: Context, var itemClick: (ReportModelClass) -> Unit) :
    RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {

    var reportList = ArrayList<ReportModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var reportImage: ImageView = itemView.findViewById(R.id.imgReport)
        var reportName: TextView = itemView.findViewById(R.id.txtTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rcv_report_image_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.reportName.text = reportList[position].reportName

        Glide.with(context).load(reportList[position].reportImage)
            .placeholder(R.drawable.ic_image).into(holder.reportImage)

        holder.itemView.setOnClickListener {
            itemClick.invoke(reportList[position])
        }
    }

    fun updateList(reportList: ArrayList<ReportModelClass>) {
        this.reportList = reportList
        notifyDataSetChanged()
    }

}