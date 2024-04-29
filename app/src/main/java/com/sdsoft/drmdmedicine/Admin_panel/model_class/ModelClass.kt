package com.sdsoft.drmdmedicine.Admin_panel.model_class

class ModelClass {
    var name: String? = null
    var uid: String? = null

    constructor(
        name: String, uid: String
    ) {
        this.name = name
        this.uid = uid
    }

    constructor() {}
}