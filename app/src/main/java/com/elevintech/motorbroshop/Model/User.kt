package com.elevintech.motorbroshop.Model

import java.io.Serializable

open class User (

    var uid: String = "",
    var shopId: String = "",

    var firstName:String = "",
    var lastName: String = "",

    var email: String = "",
    var profilePictureUrl:String = "",

    var token: String = ""
): Serializable
