package com.sdsoft.drmdmedicine.Admin_panel.adapter_class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sdsoft.drmdmedicine.Admin_panel.model_class.DiseaseModelClass
import com.sdsoft.drmdmedicine.R

class DiseaseListAdapter(var context: Context, var itemClick: (DiseaseModelClass) -> Unit) :
    RecyclerView.Adapter<DiseaseListAdapter.MyViewHolder>() {

    var diseaseList = ArrayList<DiseaseModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var diseaseName: TextView = itemView.findViewById(R.id.txtDiseaseName)
        var cdDisease: CardView = itemView.findViewById(R.id.cdDisease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rcv_disease_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return diseaseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.diseaseName.text = diseaseList[position].diseaseName


        holder.cdDisease.setOnClickListener {
            itemClick.invoke(diseaseList[position])
        }
    }

    fun updateList(diseaseList: ArrayList<DiseaseModelClass>) {
        this.diseaseList = diseaseList
        notifyDataSetChanged()
    }

}