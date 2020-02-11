package com.elevintech.motorbroshop.Register

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Employee
import com.elevintech.motorbroshop.Model.ShopOwner
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register_employee.*

class RegisterEmployee : AppCompatActivity() {

    lateinit var employee: Employee

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_employee)

        employee = intent.getSerializableExtra("employee") as Employee

        createAccountButton.setOnClickListener {

            if (hasCompletedValues())
                registerUser()

        }
    }

    fun registerUser() {

        val auth = FirebaseAuth.getInstance()

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering your profile....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                // SUCCESSFULL CREATE OF ACCOUNT
                if (task.isSuccessful) {

                    val uid = task.result!!.user!!.uid
                    val employeeId = employee.employeeId

                    val user = User( employeeId,  User.UserType.EMPLOYEE)

                    MotorBroDatabase().createNewUser(uid, user){
                        MotorBroDatabase().updateEmployeeFields(employeeId, email, uid){
                            progressDialog.dismiss()
                            val intent = Intent(applicationContext, DashboardActivity::class.java)
                            startActivity(intent)

                            finish()
                        }
                    }

                    // FAILED CREATE OF ACCOUNT
                } else {
                    Toast.makeText(baseContext, "${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }

    }

    fun hasCompletedValues(): Boolean {

        if (emailEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the email field", Toast.LENGTH_LONG).show()
            return false
        }

        if (passwordEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the password field", Toast.LENGTH_LONG).show()
            return false
        }

        if (confirmPasswordEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the confirm password field", Toast.LENGTH_LONG).show()
            return false
        }

        if (confirmPasswordEditText.text.toString() != passwordEditText.text.toString()) {
            Toast.makeText(this, "Please check that the password you entered are the same", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}
