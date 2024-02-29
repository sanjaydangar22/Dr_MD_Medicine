package com.sdsoft.drmdmedicine.Admin_panel.Patient_data.medicine

class PatientMedicineModelClass {
    var frontImage: String? = null
    var backImage: String? = null
    var medicineCompanyName: String? = null
    var medicineName: String? = null
    var medicineUnit: String? = null
    var medicineMorningTime: String? = null
    var medicineAfternoonTime: String? = null
    var medicineNightTime: String? = null
    var patientMedicineUid: String? = null

    constructor(
        frontImage: String,
        backImage: String,
        medicineCompanyName: String,
        medicineName: String,
        medicineUnit: String,
        medicineMorningTime: String,
        medicineAfternoonTime: String,
        medicineNightTime: String,
        patientMedicineUid: String
    ) {
        this.frontImage = frontImage
        this.backImage = backImage
        this.medicineCompanyName = medicineCompanyName
        this.medicineName = medicineName
        this.medicineUnit = medicineUnit
        this.medicineMorningTime = medicineMorningTime
        this.medicineAfternoonTime = medicineAfternoonTime
        this.medicineNightTime = medicineNightTime
        this.patientMedicineUid =patientMedicineUid
    }

    constructor() {}
}