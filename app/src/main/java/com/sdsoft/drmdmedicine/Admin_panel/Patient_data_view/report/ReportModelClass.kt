package com.sdsoft.drmdmedicine.Admin_panel.Patient_data_view.report

class ReportModelClass {
    var reportImage: String? = null
    var reportName: String? = null
    var reportUid: String? = null

    constructor(
        reportImage: String,
        reportName: String,
        reportUid: String
    ) {
        this.reportImage = reportImage
        this.reportName = reportName
        this.reportUid = reportUid
    }

    constructor() {}
}