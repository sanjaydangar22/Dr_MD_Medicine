package com.sdsoft.drmdmedicine.Admin_panel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdsoft.drmdmedicine.R

class MedicineListAdapter(var context: Context) :
    RecyclerView.Adapter<MedicineListAdapter.MyViewHolder>() {

    var medicineList = ArrayList<MedicineModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var medicineImage: ImageView = itemView.findViewById(R.id.imgMedicineImage)
        var medicineName: TextView = itemView.findViewById(R.id.txtMedicineName)
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
        holder.medicineName.text = medicineList[position].medicineName

        Glide.with(context).load(medicineList[position].frontImage)
            .placeholder(R.drawable.ic_image).into(holder.medicineImage)
    }

    fun updateList(medicineList: ArrayList<MedicineModelClass>) {
        this.medicineList = medicineList
        notifyDataSetChanged()
    }

}