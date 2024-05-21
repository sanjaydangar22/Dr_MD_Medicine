package com.mahakalinfoways.drmdclinic.Admin_panel.model_class

class PatientModelClass {
    var patientImage: String? = null
    var patientName: String? = null
    var patientAge: String? = null
    var patientWeight: String? = null
    var patientMobileNo: String? = null
    var patientVillage: String? = null
    var patientGender: String? = null
    var patientUid: String? = null
    var timestamp: String? = null
    var appointmentsNumber: Int = 0

    constructor(
        patientImage: String,
        patientName: String,
        patientAge: String,
        patientWeight: String,
        patientMobileNo: String,
        patientVillage: String,
        patientGender: String,
        patientUid: String,
        timestamp: String,
        appointmentsNumber: Int
    ) {
        this.patientImage = patientImage
        this.patientName = patientName
        this.patientAge = patientAge
        this.patientWeight = patientWeight
        this.patientMobileNo = patientMobileNo
        this.patientVillage = patientVillage
        this.patientGender = patientGender
        this.patientUid = patientUid
        this.timestamp = timestamp
        this.appointmentsNumber = appointmentsNumber
    }

    constructor() {}
}