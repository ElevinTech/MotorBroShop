package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Employee(

    var uid:String = "", // used for logging in (is the uid in authentication)
    var employeeId: String = "", // used for getting data (generated when shop owner creates the employee)

    var shopId: String = "",
    var branchId: String = "",


    var firstName:String = "",
    var lastName: String = "",

    var email: String = "",
    var profilePictureUrl:String = ""


): Serializable