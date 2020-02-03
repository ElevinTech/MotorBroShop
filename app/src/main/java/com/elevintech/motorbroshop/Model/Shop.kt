package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Shop (

    var name: String = "",
    var logoUrl: String = "",
    var shopId: String = "",
    var branches: MutableList<Branch> = mutableListOf()

) : Serializable