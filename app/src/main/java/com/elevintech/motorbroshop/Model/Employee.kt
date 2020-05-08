package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Employee(

    var employeeId: String = "",
    var employeeCode: String = "",
    var branchId: String = "",
    var hasSetupLogin: Boolean = false

): Serializable, User() {

}