package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Customer( var gender: String = "",
                var profileImage: String = "",
                var customParts: List<String> = listOf(),
                var customReminders: List<String> = listOf(),
                var customHistory: List<String> = listOf(),
                var customFuel: List<String> = listOf()) : Serializable, User()