package com.elevintech.motorbroshop.Customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_customer_profile.*

class CustomerProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_profile)

        val customer = intent.getSerializableExtra("customer") as Customer

        updateUI(customer)

        btnBack.setOnClickListener {
            finish()
        }

    }

    private fun updateUI(customer: Customer) {

        userNameToolbar.text = (customer.firstName + " " + customer.lastName)
        userNameFull.text = (customer.firstName + " " + customer.lastName)
    }
}
