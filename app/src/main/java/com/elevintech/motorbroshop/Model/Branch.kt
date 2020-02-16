package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Branch (


    var id: String = "",
    var address: String = "",

    var name: String = "",
    var contactNumber: String = "",

    var imageUrl: String = "",
    var isMain: Boolean = false

) : Serializable