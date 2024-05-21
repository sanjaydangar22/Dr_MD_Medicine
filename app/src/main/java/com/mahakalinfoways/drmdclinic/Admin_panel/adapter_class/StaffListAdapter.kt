package com.mahakalinfoways.drmdclinic.Admin_panel.adapter_class

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahakalinfoways.drmdclinic.Admin_panel.model_class.StaffModelClass
import com.mahakalinfoways.drmdclinic.R

class StaffListAdapter(
    var context: Context,
    var itemEdit: (String) -> Unit,
    var itemDelete: (String) -> Unit
) :
    RecyclerView.Adapter<StaffListAdapter.MyViewHolder>() {

    var staffList = ArrayList<StaffModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var staffName: TextView = itemView.findViewById(R.id.txtStaffName)
        var staffMobileNo: TextView = itemView.findViewById(R.id.txtStaffMobileNo)
        var staffEmail: TextView = itemView.findViewById(R.id.txtStaffEmail)
        var edit: ImageView = itemView.findViewById(R.id.imgEdit)
        var delete: ImageView = itemView.findViewById(R.id.imgDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rcv_staff_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return staffList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.staffName.text = staffList[position].staffName
        holder.staffMobileNo.text = staffList[position].staffMobileNumber
        holder.staffEmail.text = staffList[position].staffEmail

        holder.edit.setOnClickListener {
            itemEdit.invoke(staffList[position].staffUid!!)
        }
        holder.delete.setOnClickListener {
            itemDelete.invoke(staffList[position].staffUid!!)
        }
    }

    fun updateList(staffList: ArrayList<StaffModelClass>) {
        this.staffList = staffList
        notifyDataSetChanged()
    }

}