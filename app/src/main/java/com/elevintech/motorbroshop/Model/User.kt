package com.elevintech.motorbroshop.Model

import java.io.Serializable

class User (

    var uid: String = "",
    var userType: String = ""

): Serializable{

    object UserType {
        val OWNER = "owner"
        val EMPLOYEE = "employee"
    }


}

