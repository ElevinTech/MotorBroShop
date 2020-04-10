package com.elevintech.motorbroshop

import android.app.ProgressDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    fun easyProgressDialog(context: Context, text: String): ProgressDialog {
        var progressDialog = ProgressDialog(context)
        progressDialog.setMessage(text)
        progressDialog.setCancelable(false)

        return progressDialog
    }

    fun getCurrentTimestamp():Long{
        return System.currentTimeMillis() / 1000
    }

    fun convertMillisecondsToDate(dateInMilliseconds: Long , datePatternFormat: String) : String{

        val sdf = SimpleDateFormat("$datePatternFormat") // sample date format: MM/dd/yyyy hh:mm
        val netDate = Date(dateInMilliseconds)

        return sdf.format(netDate).toString()
    }

    fun convertMillisecondsToDate(dateInMilliseconds: String , datePatternFormat: String) : String{

        val sdf = SimpleDateFormat("$datePatternFormat")

        var newDate = dateInMilliseconds
        if(newDate.count() == 10) {
            newDate += "000"// sample date format: "h:mm a"	12:08 PM / "EEE, MMM d, ''yy"	Wed, Jul 4, '01
        }
        val netDate = Date(newDate.toLong())

        return sdf.format(netDate).toString()
    }

    fun convertDateToMilliseconds(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int): Long{

        val cal = Calendar.getInstance()
        cal.set(year, month, day, hour, minute, seconds)

        return cal.timeInMillis

    }

    fun monthInWords(monthString: String): String {

        var month = ""
        when(monthString.toInt()) {

            1 -> { month = "January" }
            2 -> { month = "February" }
            3 -> { month = "March" }
            4 -> { month = "April" }

            5 -> { month = "May" }
            6 -> { month = "June" }
            7 -> { month = "July" }
            8 -> { month = "August" }

            9 -> { month = "September" }
            10 -> { month = "October" }
            11 -> { month = "November" }
            12 -> { month = "December" }

        }

        return month
    }

    fun showProgressDialog(context: Context, message: String): ProgressDialog{
        var progressDialog = ProgressDialog(context)
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()

        return progressDialog
    }

    fun convertDateToTimestamp(date: String, format: String):Long{

        val unixTime = SimpleDateFormat(format).parse( date ).time.toString().toLong()
        val timestamp = unixTime / 1000

        return timestamp

    }


}