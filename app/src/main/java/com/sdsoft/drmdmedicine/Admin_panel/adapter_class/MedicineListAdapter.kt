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
import com.sdsoft.drmdmedicine.Admin_panel.model_class.ModelClass
import com.sdsoft.drmdmedicine.R

class MedicineListAdapter(var context: Context, var itemClick: (ModelClass) -> Unit) :
    RecyclerView.Adapter<MedicineListAdapter.MyViewHolder>() {

    var medicineList = ArrayList<ModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var medicineName: TextView = itemView.findViewById(R.id.txtMedicineName)
        var cdMedicine: CardView = itemView.findViewById(R.id.cdMedicine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rcv_medicine_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return medicineList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.medicineName.text = medicineList[position].name



        holder.cdMedicine.setOnClickListener {
            itemClick.invoke(medicineList[position])
        }
    }

    fun updateList(medicineList: ArrayList<ModelClass>) {
        this.medicineList = medicineList
        notifyDataSetChanged()
    }

}