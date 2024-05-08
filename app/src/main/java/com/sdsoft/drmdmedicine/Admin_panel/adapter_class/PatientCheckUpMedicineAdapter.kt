package com.sdsoft.drmdmedicine.Admin_panel.adapter_class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ModelClass
import com.sdsoft.drmdmedicine.Admin_panel.model_class.PatientMedicineModel
import com.sdsoft.drmdmedicine.R

class PatientCheckUpMedicineAdapter(
    var context: Context, var itemClick: (PatientMedicineModel) -> Unit
) : RecyclerView.Adapter<PatientCheckUpMedicineAdapter.MyViewHolder>() {

    var list = ArrayList<PatientMedicineModel>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtMedicineName: TextView = itemView.findViewById(R.id.txtMedicineName)
        var txtMedicineQty: TextView = itemView.findViewById(R.id.txtMedicineQty)
        var txtMedicineTime: TextView = itemView.findViewById(R.id.txtMedicineTime)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rcv_patinent_medicine_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtMedicineName.text = list[position].name
        holder.txtMedicineQty.text = list[position].qty
        holder.txtMedicineTime.text = list[position].time

        holder.itemView.setOnClickListener {
            itemClick.invoke(list[position])
        }

    }

    fun updateList(updatedList: ArrayList<PatientMedicineModel>) {
        this.list = updatedList
        notifyDataSetChanged()
    }
}
