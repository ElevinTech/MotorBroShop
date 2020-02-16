package com.elevintech.motorbroshop.Employees

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.elevintech.motorbroshop.Constants.REQUEST_CODES.SELECT_BRANCH
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Branch
import com.elevintech.motorbroshop.Model.Employee
import com.elevintech.motorbroshop.Model.ShopOwner
import com.elevintech.motorbroshop.Model.ShopUser
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_employee.*

class CreateEmployeeActivity : AppCompatActivity() {

    var branchId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_employee)

        var shopId = intent.getStringExtra("shopId")

        branchEditText.setOnClickListener {

            val intent = Intent(applicationContext, SelectBranchActivity::class.java)
            intent.putExtra("shopId", shopId)
            startActivityForResult(intent, SELECT_BRANCH)

        }

        createAccountButton.setOnClickListener {

            // SHOW PROGRESS DIALOG
            val progressDialog = Utils().easyProgressDialog(this, "Creating Employee")
            progressDialog.show()

            // CREATE EMPLOYEE OBJECT
            var employee = Employee()
            employee.firstName = firstNameEditText.text.toString()
            employee.lastName = lastNameEditText.text.toString()
            employee.shopId = shopId
            employee.employeeId = FirebaseFirestore.getInstance().collection("employees").document().id
            employee.branchId = branchId
            employee.profilePictureUrl = "" // TODO: set profile picture

            // SAVE IN FIRESTORE
            MotorBroDatabase().createEmployee(employee){
               progressDialog.dismiss()
                createBasicAlertDialog(employee)
            }


        }

     }

    fun createBasicAlertDialog(employee: Employee){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success!")
        builder.setMessage("Your employee's ID is: '" + employee.employeeId + "', they will use this to create their own login account.")
        builder.setPositiveButton("Got it") { dialog, which ->

            dialog.dismiss()
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data != null){
                if (requestCode == SELECT_BRANCH){
                    var branch = data!!.getSerializableExtra("branch") as Branch
                    branchEditText.setText(branch.name)
                    branchId = branch.id
                }
            }
        }
    }
}
