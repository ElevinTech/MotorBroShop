package com.elevintech.motorbroshop.AddTypeOfParts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import kotlinx.android.synthetic.main.activity_add_brand.*

class AddBrandActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_brand)

        saveButton.setOnClickListener{

            val showProgressDialog = Utils().showProgressDialog(this, "Saving custom brand....")

            MotorBroDatabase().saveCustomBrand(addTypeOfPartsText.text.toString()){
                showProgressDialog.dismiss()
                finish()
            }
        }

        backView.setOnClickListener {
            finish()
        }
    }
}
