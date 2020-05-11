package com.elevintech.motorbroshop.Model

import android.annotation.SuppressLint
import java.io.Serializable

class BikeInfo(): Serializable {
    var id: String = ""
    var userId: String = ""
    var brand: String = ""
    var model: String = ""
    var plateNumber: String = ""
    var odometer: String = ""
    var nickname: String = ""
    var fuelLiter: Double = 0.0
    var odometerValue: Double = 0.0
    var income: Double = 0.0
    var yearBought: String = ""
    var imageUrl: String = ""
    var bikeId: String = ""
    var deleted: Boolean = false
    var primary: Boolean = false
    var lastOdometerUpdate: String = ""


    fun getUserDate(): String {
        var result = ""

        if (yearBought.length > 4)
        {
            result = yearBought.substring(0, 4);
        } else {
            result = yearBought
        }

        return result
    }

    @SuppressLint("DefaultLocale")
    fun getUserBikeName(): String {
        return getUserDate() + " " +brand.capitalize() + " " + model.capitalize()
    }
}