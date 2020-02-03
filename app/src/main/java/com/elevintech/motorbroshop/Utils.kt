package com.elevintech.motorbroshop

import android.app.ProgressDialog
import android.content.Context

class Utils {

    fun easyProgressDialog(context: Context, text: String): ProgressDialog {
        var progressDialog = ProgressDialog(context)
        progressDialog.setMessage(text)
        progressDialog.setCancelable(false)

        return progressDialog
    }

}