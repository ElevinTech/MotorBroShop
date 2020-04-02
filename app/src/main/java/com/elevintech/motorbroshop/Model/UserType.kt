package com.elevintech.motorbroshop.Model

import java.io.Serializable

class UserType(
    var uid: String = "",
    var userType: String = "",
    var token: String = ""

): Serializable{

    object Type {
        val OWNER = "owner"
        val EMPLOYEE = "employee"
        val CUSTOMER = "customer"
    }


}

