package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Document (

    var type: String = "",
    var imageUrl: String = ""

) : Serializable {


    object DocumentType {
        val BIR = "bir"
        val BARANGGAY_PERMIT = "brgy_permit"
    }

}