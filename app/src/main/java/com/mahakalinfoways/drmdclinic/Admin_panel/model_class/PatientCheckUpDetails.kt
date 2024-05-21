package com.mahakalinfoways.drmdclinic.Admin_panel.model_class


import java.text.SimpleDateFormat
import java.util.*

class PatientCheckUpDetails {
    var date: String? = null
    fun getMonth(): Int {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateObj = dateFormat.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = dateObj
        return calendar.get(Calendar.MONTH)
    }
    constructor(date: String) {
        this.date = date
    }

    constructor() {}
}


