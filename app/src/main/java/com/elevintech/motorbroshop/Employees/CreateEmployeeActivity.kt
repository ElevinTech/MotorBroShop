package com.elevintech.motorbroshop.Employees

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Employee
import com.elevintech.motorbroshop.Model.ShopOwner
import com.elevintech.motorbroshop.Model.ShopUser
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_employee.*

class CreateEmployeeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_employee)

        var owner = intent.getSerializableExtra("owner") as ShopOwner

        createAccountButton.setOnClickListener {

            // SHOW PROGRESS DIALOG
            val progressDialog = Utils().easyProgressDialog(this, "Creating Employee")
            progressDialog.show()

            // CREATE EMPLOYEE OBJECT
            var employee = Employee()
            employee.firstName = firstNameEditText.text.toString()
            employee.lastName = lastNameEditText.text.toString()
            employee.email = emailEditText.text.toString()
            employee.shopId = owner.shopId
            employee.employeeId = FirebaseFirestore.getInstance().collection("employees").document().id
            employee.branchId = "" // TODO: set employee branch id
            employee.profilePictureUrl = "" // TODO: set profile picture

            // SAVE IN FIRESTORE
            MotorBroDatabase().createEmployee(employee){
               progressDialog.dismiss()
               finish()
            }


        }

     }

}
