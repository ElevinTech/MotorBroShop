package com.elevintech.motorbroshop.Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_select_user_type.*

class SelectUserType : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user_type)

        employeeButton.setOnClickListener {
            val intent = Intent(applicationContext, EnterEmployeeID::class.java)
            startActivity(intent)
            finish()
        }


        ownerButton.setOnClickListener {
            val intent = Intent(applicationContext, RegisterOwner::class.java)
            startActivity(intent)
            finish()
        }


    }
}
