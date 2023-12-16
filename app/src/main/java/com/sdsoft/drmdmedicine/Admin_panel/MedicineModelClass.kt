package com.sdsoft.drmdmedicine.Admin_panel

class MedicineModelClass {
    var frontImage: String? = null
    var backImage: String? = null
    var medicineCompanyName: String? = null
    var medicineName: String? = null
    var medicineUse: String? = null
    var medicineUid: String? = null

    constructor(
        frontImage: String,
        backImage: String,
        medicineCompanyName: String,
        medicineName: String,
        medicineUse: String,
        medicineUid: String
    ) {
        this.frontImage = frontImage
        this.backImage = backImage
        this.medicineCompanyName = medicineCompanyName
        this.medicineName = medicineName
        this.medicineUse = medicineUse
        this.medicineUid = medicineUid
    }

    constructor() {}
}