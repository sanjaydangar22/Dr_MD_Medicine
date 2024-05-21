package com.mahakalinfoways.drmdclinic.Admin_panel.adapter_class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahakalinfoways.drmdclinic.Admin_panel.model_class.ModelClass
import com.mahakalinfoways.drmdclinic.R

class PatientCheckUpAdapter(
    var context: Context, var delete: (String) -> Unit
) : RecyclerView.Adapter<PatientCheckUpAdapter.MyViewHolder>() {

    var list = ArrayList<ModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTextView: TextView = itemView.findViewById(R.id.txtTextView)
        var imgDeleteBtn: ImageView = itemView.findViewById(R.id.imgDeleteBtn)
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
        holder.txtTextView.text = list[position].name
        holder.imgDeleteBtn.setOnClickListener {
            delete.invoke(list[position].uid!!)
        }

    }

    fun updateList(updatedList: ArrayList<ModelClass>) {
        this.list = updatedList
        notifyDataSetChanged()
    }
}
