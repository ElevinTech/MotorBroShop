package com.elevintech.motorbroshop

class Constants {


    object REQUEST_CODES{
        val SELECT_BRANCH = 10
    }

    object TYPE_OF{

        val parts = listOf("Motor Oil",
            "Chain",
            "Sprocket",
            "Chain and Sprocket Set",
            "Brake Shoe",
            "Brake Pad",
            "Tire",
            "Interior Tube",
            "Bulb",
            "C.D.I Rectifier",
            "Spark Plug",
            "Shock Absorber",
            "V-Belt",
            "Air Filter",
            "Bearing",
            "Clutch Lining",
            "Front CVT Pulley",
            "Rear CVT Pulley",
            "Mag Wheels",
            "Spoke and Rim hub",
            "Oil Filter",
            "Gasket",
            "Piston",
            "Connecting Rod",
            "Axle",
            "Horn",
            "Light")

        val brands = listOf("GPC",
            "Osaki",
            "Samurai",
            "Comstar",
            "E-Power",
            "Owens",
            "TSR",
            "Tiger",
            "Keyster",
            "OKK",
            "Motmot",
            "ND Rubber",
            "Cyclefix")

        val fuel = listOf("Unleaded", "Premium")

        val reminders = listOf("Reminder Default 1", "Reminder Default 2")
        val history = listOf("Income", "Service", "Expenses", "Refueling")

    }

    object AD_TYPE {
        const val SAMURAI_PAINT = "SAMURAI_PAINT"
        const val OWENS = "OWENS"
        const val TSR = "TSR"
        const val OSAKI = "OSAKI"
        const val E_POWER = "EPOWER"
        const val COMSTAR = "COMSTAR"
        const val CYCLEFIX = "CYCLEFIX"
        const val TIGER = "TIGER"
        const val OKK = "OKK"
        const val MOTMOT = "MOTMOT"
        const val POSH = "POSH"
        const val GPC = "GPC"

    }
}