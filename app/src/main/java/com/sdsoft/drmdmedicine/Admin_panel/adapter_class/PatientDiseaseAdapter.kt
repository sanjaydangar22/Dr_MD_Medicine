package com.sdsoft.drmdmedicine.Admin_panel.adapter_class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdsoft.drmdmedicine.Admin_panel.model_class.DiseaseModelClass
import com.sdsoft.drmdmedicine.R

class PatientDiseaseAdapter(
    var context: Context
) : RecyclerView.Adapter<PatientDiseaseAdapter.MyViewHolder>() {

    var list = ArrayList<DiseaseModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTextView: TextView = itemView.findViewById(R.id.txtTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rcv_patinent_disease_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtTextView.text = list[position].diseaseName

    }

    fun updateList(updatedList: ArrayList<DiseaseModelClass>) {
       this.list=updatedList
        notifyDataSetChanged()
    }
}
