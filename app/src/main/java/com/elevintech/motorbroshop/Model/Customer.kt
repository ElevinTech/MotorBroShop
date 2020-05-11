package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Customer( var gender: String = "",
                var profileImage: String = "",
                var dateAdded: String = "") : Serializable, User()