package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Employee(

    var employeeId: String = "", // used for getting data (generated when shop owner creates the employee)
    var branchId: String = "",
    var hasSetupLogin: Boolean = false

): Serializable, User() {

}