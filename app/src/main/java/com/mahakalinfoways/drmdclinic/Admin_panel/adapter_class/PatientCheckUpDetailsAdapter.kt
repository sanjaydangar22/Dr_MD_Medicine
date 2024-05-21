package com.mahakalinfoways.drmdclinic.Admin_panel.adapter_class

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahakalinfoways.drmdclinic.Admin_panel.model_class.PatientCheckUpDetails
import com.mahakalinfoways.drmdclinic.R

class PatientCheckUpDetailsAdapter(
    private val checkUpDetails: List<PatientCheckUpDetails>,
    var itemClick: (PatientCheckUpDetails) -> Unit
) : RecyclerView.Adapter<PatientCheckUpDetailsAdapter.ViewHolder>() {
    var selectedItemPosition = -1
    private val sortedCheckUpDetails: List<PatientCheckUpDetails>

    init {
        // Sort the checkUpDetails based on month and date in descending order
        sortedCheckUpDetails =
            checkUpDetails.sortedWith(compareByDescending<PatientCheckUpDetails> { it.getMonth() }.thenByDescending { it.date })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.txtCheckUpDate)
        val linBackground: LinearLayout = itemView.findViewById(R.id.linBackground)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rcv_check_up_details_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkUp = sortedCheckUpDetails[position]

        holder.apply {
            dateTextView.text = checkUp.date

            // Handle item click event
            itemView.setOnClickListener {
                selectedItemPosition = position
                itemClick.invoke(checkUp)
                notifyDataSetChanged()
            }

            if (selectedItemPosition == position) {
                linBackground.setBackgroundColor(Color.parseColor("#2196F3"))
                dateTextView.setTextColor(Color.parseColor("#ffffff"))
            } else {
                linBackground.setBackgroundColor(Color.parseColor("#CDF5FD"))
                dateTextView.setTextColor(Color.parseColor("#000000"))
            }
        }
    }

    override fun getItemCount(): Int {
        return sortedCheckUpDetails.size
    }


}
