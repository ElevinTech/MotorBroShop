package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Shop (

    var shopId: String = "",
    var ownerId: String = "",

    var name: String = "",
    var logoUrl: String = "",

    var dateEstablished: String = "",

    var description: String = "",
    var address: String = "",

    var imageUrl: String = ""

) : Serializable