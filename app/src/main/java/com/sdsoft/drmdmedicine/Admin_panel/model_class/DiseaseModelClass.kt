package com.sdsoft.drmdmedicine.Admin_panel.model_class

class DiseaseModelClass {
    var diseaseName: String? = null
    var diseaseUid: String? = null

    constructor(
         diseaseName: String,  diseaseUid: String
    ) {
        this.diseaseName = diseaseName
        this.diseaseUid = diseaseUid
    }

    constructor() {}
}