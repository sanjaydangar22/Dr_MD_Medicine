package com.mahakalinfoways.drmdclinic.Admin_panel.model_class

class StaffModelClass {
    var staffName: String? = null
    var staffMobileNumber: String? = null
    var staffEmail: String? = null
    var staffPassword: String? = null
    var staffUid: String? = null

    constructor(
        staffName: String,
        staffMobileNumber: String,
        staffEmail: String,
        staffPassword: String,
        staffUid: String
    ) {
        this.staffName = staffName
        this.staffMobileNumber = staffMobileNumber
        this.staffEmail = staffEmail
        this.staffPassword = staffPassword
        this.staffUid = staffUid
    }

    constructor() {}
}