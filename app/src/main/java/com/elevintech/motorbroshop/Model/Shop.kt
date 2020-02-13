package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Shop (

    var shopId: String = "",
    var ownerId: String = "",

    var name: String = "",
    var logoUrl: String = "",

    var documents: MutableList<Document> = mutableListOf(),
    var branches: MutableList<Branch> = mutableListOf()

) : Serializable