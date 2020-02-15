package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Customer( var uid: String = "",
                var firstName: String = "",
                var lastName: String = "",
                var gender: String = "",
                var email: String = "",
                var customParts: List<String> = listOf(),
                var customReminders: List<String> = listOf(),
                var customHistory: List<String> = listOf(),
                var customFuel: List<String> = listOf()) : Serializable