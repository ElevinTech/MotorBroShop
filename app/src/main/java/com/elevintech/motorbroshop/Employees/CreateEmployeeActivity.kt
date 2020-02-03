package com.elevintech.motorbroshop.Employees

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.ShopUser
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_employee.*

class CreateEmployeeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_employee)

       MotorBroDatabase().getUser {

           val owner = it

           createAccountButton.setOnClickListener {

                val progressDialog = Utils().easyProgressDialog(this, "Creating Employee")
                progressDialog.show()

                MotorBroDatabase().generateEmployeeId{

                    val employeeId = it
                    val firstName = firstNameEditText.text.toString()
                    val lastName = lastNameEditText.text.toString()
                    val email = emailEditText.text.toString()
                    val shopId = owner.shopId

                    val employee = ShopUser(firstName, lastName, email, employeeId, shopId, false, "")

                    MotorBroDatabase().createEmployee(employee){
                        progressDialog.dismiss()

                        finish()
                    }
                }
            }

        }


    }
}
