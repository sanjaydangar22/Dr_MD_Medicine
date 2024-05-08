package com.sdsoft.drmdmedicine.Admin_panel.model_class

class PatientMedicineModel {
    var name: String? = null
    var qty: String? = null
    var time: String? = null
    var uid: String? = null

    constructor(
        name: String, qty: String, time: String, uid: String
    ) {
        this.name = name
        this.qty = qty
        this.time = time
        this.uid = uid
    }

    constructor() {}
}