package com.sdsoft.drmdmedicine.Admin_panel.model_class

class PatientModelClass {
    var patientImage: String? = null
    var patientName: String? = null
    var patientAge: String? = null
    var patientGender: String? = null
    var patientMobileNo: String? = null
    var patientUid: String? = null

    constructor(
        patientImage: String,
        patientName: String,
        patientAge: String,
        patientGender: String,
        patientMobileNo: String,
        patientUid: String
    ) {
        this.patientImage = patientImage
        this.patientName = patientName
        this.patientAge = patientAge
        this.patientGender = patientGender
        this.patientMobileNo = patientMobileNo
        this.patientUid = patientUid
    }

    constructor() {}
}