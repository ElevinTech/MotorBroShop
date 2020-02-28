package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Document (

    var type: String = "",
    var imageUrl: String = "",
    var dateModified: Long = 0,
    var status: String = "unverified"

) : Serializable {

    object DocumentType {
        val DTI: String = "dti"
        val MAYORS_PERMIT: String = "mayors_permit"
        val BIR: String = "bir"
    }

}