package com.elevintech.motorbroshop.Model

import java.io.Serializable

// INHERITS the SHOP class
class Branch (

    var branchId: String = ""

) : Serializable, Shop()