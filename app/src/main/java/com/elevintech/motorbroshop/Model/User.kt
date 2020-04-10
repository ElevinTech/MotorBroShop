package com.elevintech.motorbroshop.Model

import java.io.Serializable

open class User (

    var uid: String = "",
    var shopId: String = "",

    var firstName:String = "",
    var lastName: String = "",

    var email: String = "",
    var profilePictureUrl:String = "",

    var token: String = "",
    var customParts: List<String> = listOf(),
    var deletedParts: List<String> = listOf(),
    var deletedBrands: List<String> = listOf(),
    var customBrands: List<String> = listOf()
): Serializable
